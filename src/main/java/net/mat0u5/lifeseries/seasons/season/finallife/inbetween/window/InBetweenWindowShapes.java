package net.mat0u5.lifeseries.seasons.season.finallife.inbetween.window;

import java.util.List;

public class InBetweenWindowShapes {
	public static final float CELL_SIZE = 0.75f; // Size of a single cell in blocks

	private static final String[] SHAPE_1 = { // Grian Death 1 Window 1
			"...#####.......",
			"..########.....",
			".#########.....",
			"###########....",
			"#############..",
			"###############",
			".##############",
			"..######......."
	};

	private static final String[] SHAPE_2 = { // Grian Death 1 Window 2
			".....#####...",
			".....######..",
			"....#########",
			"..###########",
			"#############",
			"#############",
			".......######"
	};

	private static final String[] SHAPE_3 = { // Grian Death 2 Window 1
			".#####.....",
			"######.....",
			"#######....",
			"#########..",
			"..#########",
			"....#######",
	};

	private static final String[] SHAPE_4 = { // Grian Death 2 Window 2
			"......######...",
			"......######...",
			"......#######..",
			"...###########.",
			"..############.",
			".#############.",
			"##############.",
			"##############.",
			"###############",
			".##############",
			"..######.......",
	};

	//TODO more window shapes

	private static final List<String[]> SHAPES = List.of(SHAPE_1, SHAPE_2, SHAPE_3, SHAPE_4);

	public static String[] get(int deathNumber, int windowNumber) {
		int index = ((deathNumber - 1) * 2 + (windowNumber - 1)) % SHAPES.size();
		return SHAPES.get(index);
	}
}
