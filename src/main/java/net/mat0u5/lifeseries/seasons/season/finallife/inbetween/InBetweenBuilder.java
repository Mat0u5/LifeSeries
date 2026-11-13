package net.mat0u5.lifeseries.seasons.season.finallife.inbetween;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.matlib.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Structures - {@value #STRUCTURE_CELL_SIZE} block cells in all three directions, every slice must be exactly on that grid.
 *		{@code in_between_<x>_<y>_<z>} is used, if missing, {@code in_between_default_<x>_<y>_<z} is used (builtin).
 *			<x>_<y>_<z> refer to the placement in the cell grid, not the world. Origin is at 0_0_0.
 *
 * Structure Data:
 * 		{@code spawn} - spawn position, use {@code spawn <yaw>} to face a different direction.
 *		{@code choice 1}, {@code choice 2} ... centers of each choice ring.
 *				{@code choice 1 <x> <y> <z>} - moves the choice ring preciely.
 *		{@code path 1}, {@code path 2}, ... - path waypoints, connected by straight lines
 *
 *	In case more players are in the dimension at once, they are spaced out by {@link #PLAYER_SLOT_SPACING}.
 */
public class InBetweenBuilder {
	public static final String STRUCTURE_PREFIX = "in_between_";
	public static final String DEFAULT_STRUCTURE_PREFIX = "in_between_default_";
	public static final int STRUCTURE_CELL_SIZE = 48;
	private static final int MAX_CELL_X = 8; // Prevent in between instances touching
	public static final int PLAYER_SLOT_SPACING = 1024;
	public static final int ORIGIN_Y = 96;
	private static final int CLEAR_MARGIN = 16; // Area to clear around structures
	private final List<Slice> slices;
	private final BlockPos origin;
	private final AABB structureBox;
	private final InBetweenInstance inBetween;
	private double minMarkerY;
	public final List<Vec3> pathPoints = new ArrayList<>();
	public final ServerLevel level;
	public Vec3 spawnPos = Vec3.ZERO;
	public float spawnYaw = 0;

	public InBetweenBuilder(InBetweenInstance inBetween, List<Slice> slices, int inBetweenSlot, ServerLevel level) {
		this.inBetween = inBetween;
		this.slices = slices;

		Slice originSlice = slices.get(0);
		for (Slice slice : slices) {
			if (slice.cell().equals(Vec3i.ZERO)) {
				originSlice = slice;
				break;
			}
		}
		int originSizeX = originSlice.template().getSize().getX();
		this.origin = new BlockPos(inBetweenSlot * PLAYER_SLOT_SPACING - originSizeX / 2, ORIGIN_Y, 0);
		this.spawnPos = Vec3.atBottomCenterOf(getSlicePos(originSlice).offset(originSizeX / 2, 1, 1)); // Temporary, before marker info is loaded
		AABB structBox = null;
		for (Slice slice : slices) {
			BlockPos slicePos = getSlicePos(slice);
			Vec3i sliceSize = slice.template().getSize();
			AABB sliceBox = new AABB(slicePos.getX(), slicePos.getY(), slicePos.getZ(), slicePos.getX() + sliceSize.getX(), slicePos.getY() + sliceSize.getY(), slicePos.getZ() + sliceSize.getZ());
			structBox = structBox == null ? sliceBox : structBox.minmax(sliceBox);
		}
		this.structureBox = structBox;
		this.minMarkerY = spawnPos.y;
		this.level = level;
	}


	public record Slice(Vec3i cell, StructureTemplate template) {}

	public static List<Slice> loadStructureSlices(ServerLevel level, String prefix) {
		//? if <= 26.2 {
		//StructureTemplateManager manager = level.getStructureManager();
		//?} else {
		StructureTemplateManager manager = level.getStructureTemplateManager();
		//?}
		Map<Vec3i, Identifier> idsByCell = new HashMap<>();
		for (Identifier id : manager.listTemplates().toList()) {
			if (!id.getNamespace().equals(LifeSeries.MOD_ID) || !id.getPath().startsWith(prefix)) continue;
			Vec3i cell = parseCell(id.getPath().substring(prefix.length()));
			if (cell == null) continue;
			if (Math.abs(cell.getX()) > MAX_CELL_X) {
				LifeSeries.LOGGER.warn("[In-Between] Ignoring structure {} as it's X coordinate is more than {} away from 0.", id, MAX_CELL_X);
				continue;
			}
			Identifier previous = idsByCell.putIfAbsent(cell, id);
			if (previous != null && !previous.equals(id)) {
				LifeSeries.LOGGER.warn("[In-Between] Structures {} and {} are in the same cell, ignoring {}.", previous, id, id);
			}
		}

		List<Slice> result = new ArrayList<>();
		for (Map.Entry<Vec3i, Identifier> entry : idsByCell.entrySet()) {
			Vec3i cell = entry.getKey();
			int minY = ORIGIN_Y + cell.getY() * STRUCTURE_CELL_SIZE;
			manager.get(entry.getValue()).ifPresent(template -> {
				if (level.isOutsideBuildHeight(minY) || level.isOutsideBuildHeight(minY + template.getSize().getY() - 1)) {
					LifeSeries.LOGGER.warn("[In-Between] Ignoring structure {} - outside dimension height limit.", entry.getValue());
					return;
				}
				result.add(new Slice(cell, template));
			});
		}
		result.sort(Comparator.comparingInt((Slice slice) -> slice.cell().getY())
				.thenComparingInt(slice -> slice.cell().getZ())
				.thenComparingInt(slice -> slice.cell().getX()));
		return result;
	}

	@Nullable
	private static Vec3i parseCell(String text) {
		String[] parts = text.split("_");
		if (parts.length == 1) {
			Integer z = parseInt(parts[0]);
			if (z == null) return null;
			return new Vec3i(0, 0, z);
		}
		if (parts.length == 3) {
			Integer x = parseInt(parts[0]);
			Integer y = parseInt(parts[1]);
			Integer z = parseInt(parts[2]);
			if (x == null || y == null || z == null) return null;
			return new Vec3i(x, y, z);
		}
		return null;
	}



	private BlockPos getSlicePos(Slice slice) {
		Vec3i cell = slice.cell();
		return origin.offset(cell.getX() * STRUCTURE_CELL_SIZE, cell.getY() * STRUCTURE_CELL_SIZE, cell.getZ() * STRUCTURE_CELL_SIZE);
	}

	private AABB getStructureBox() {
		return structureBox;
	}

	// The area that gets cleared.
	public AABB getRegion() {
		return getStructureBox().inflate(CLEAR_MARGIN);
	}
	public void build() {
		clearRegion();
		removeEntities();

		StructurePlaceSettings placeSettings = new StructurePlaceSettings().addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
		boolean foundSpawn = false;
		Map<Integer, Vec3> pathByNumber = new TreeMap<>();
		for (Slice slice : slices) {
			StructureTemplate template = slice.template();
			BlockPos slicePos = getSlicePos(slice);
			template.placeInWorld(level, slicePos, slicePos, placeSettings, level.getRandom(), Block.UPDATE_CLIENTS);

			for (StructureTemplate.StructureBlockInfo info : template.filterBlocks(slicePos, placeSettings, Blocks.STRUCTURE_BLOCK)) {
				if (info.state().getValue(StructureBlock.MODE) != StructureMode.DATA) continue;
				String[] split = getMetadata(info.nbt()).trim().toLowerCase(Locale.ROOT).split("[\\s_:=,]+");
				if (split.length == 0 || split[0].isEmpty()) continue;
				Vec3 markerPos = Vec3.atBottomCenterOf(info.pos());
				Integer firstNum = split.length > 1 ? parseInt(split[1]) : null;
				if (split[0].equalsIgnoreCase("spawn")) {
					//spawn [yaw]
					foundSpawn = true;
					spawnPos = markerPos;
					if (firstNum != null) {
						spawnYaw = firstNum;
					}
					else if (split.length > 1) {
						spawnYaw = parseFloat(split[1], 0);
					}
				}
				else if (split[0].equalsIgnoreCase("choice")) {
					// choice <number> [x offset] [y offset] [z offset]
					Vec3 offset = new Vec3(
							split.length > 2 ? parseFloat(split[2], 0) : 0,
							split.length > 3 ? parseFloat(split[3], 0) : 0,
							split.length > 4 ? parseFloat(split[4], 0) : 0
					);
					inBetween.choiceManager.choicePositions.put(firstNum == null ? inBetween.choiceManager.choicePositions.size() + 1 : firstNum, markerPos.add(offset));
				}
				else if (split[0].equalsIgnoreCase("path")) {
					//path <number>
					pathByNumber.put(firstNum == null ? pathByNumber.size() + 1 : firstNum, markerPos);
				}
				else {
					LifeSeries.LOGGER.warn("[In-Between] Unknown data marker '{}' at {}", getMetadata(info.nbt()), info.pos());
				}
			}
		}
		pathPoints.addAll(pathByNumber.values());

		if (!foundSpawn) {
			LifeSeries.LOGGER.warn("[In-Between] The In-Between structure has no data marker to set the spawn position.");
		}
		minMarkerY = spawnPos.y;
		for (Vec3 pos : inBetween.choiceManager.choicePositions.values()) minMarkerY = Math.min(minMarkerY, pos.y);
		for (Vec3 pos : pathPoints) minMarkerY = Math.min(minMarkerY, pos.y);
		inBetween.choiceManager.spawnChoiceLabels();
	}

	private static String getMetadata(@Nullable CompoundTag nbt) {
		if (nbt == null) return "";
		//? if <= 1.21.4 {
		//return nbt.getString("metadata");
		//?} else {
		return nbt.getStringOr("metadata", "");
		//?}
	}

	@Nullable
	private static Integer parseInt(String text) {
		try {
			return Integer.parseInt(text);
		} catch (Exception e) {
			return null;
		}
	}

	private static float parseFloat(String text, float fallback) {
		try {
			return Float.parseFloat(text);
		} catch (Exception e) {
			return fallback;
		}
	}

	public void clearRegion() {
		AABB region = getRegion();
		int minX = (int) Math.floor(region.minX);
		int minY = Math.max((int) Math.floor(region.minY), level.getMinY());
		int minZ = (int) Math.floor(region.minZ);
		int maxX = (int) Math.ceil(region.maxX) - 1;
		int maxY = Math.min((int) Math.ceil(region.maxY) - 1, level.getMaxY() - 1);
		int maxZ = (int) Math.ceil(region.maxZ) - 1;
		BlockState air = Blocks.AIR.defaultBlockState();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int cx = SectionPos.blockToSectionCoord(minX); cx <= SectionPos.blockToSectionCoord(maxX); cx++) {
			for (int cz = SectionPos.blockToSectionCoord(minZ); cz <= SectionPos.blockToSectionCoord(maxZ); cz++) {
				ChunkAccess chunk = level.getChunk(cx, cz);
				int chunkMinX = SectionPos.sectionToBlockCoord(cx);
				int chunkMinZ = SectionPos.sectionToBlockCoord(cz);
				for (int sectionY = SectionPos.blockToSectionCoord(minY); sectionY <= SectionPos.blockToSectionCoord(maxY); sectionY++) {
					LevelChunkSection section = chunk.getSection(chunk.getSectionIndexFromSectionY(sectionY));
					if (section.hasOnlyAir()) continue;
					int sectionMinY = SectionPos.sectionToBlockCoord(sectionY);
					for (int x = Math.max(minX, chunkMinX); x <= Math.min(maxX, chunkMinX + 15); x++) {
						for (int z = Math.max(minZ, chunkMinZ); z <= Math.min(maxZ, chunkMinZ + 15); z++) {
							for (int y = Math.max(minY, sectionMinY); y <= Math.min(maxY, sectionMinY + 15); y++) {
								pos.set(x, y, z);
								if (!level.getBlockState(pos).isAir()) {
									level.setBlock(pos, air, Block.UPDATE_CLIENTS);
								}
							}
						}
					}
				}
			}
		}
	}

	public void removeEntities() {
		for (Entity entity : level.getEntitiesOfClass(Entity.class, getRegion(), entity -> !(entity instanceof Player))) {
			entity.discard();
		}
	}

	public boolean isInsideBounds(Vec3 pos) {
		AABB box = getStructureBox().inflate(0.5, 0, 0.5).expandTowards(0, 4, 0);
		if (!box.contains(pos)) return false;
		return pos.y >= minMarkerY - 4; // Prevent void falls.
	}
}
