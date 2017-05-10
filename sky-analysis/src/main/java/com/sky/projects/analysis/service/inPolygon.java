package com.sky.projects.analysis.service;

public class inPolygon {
	public static void main(String[] args) {
		String la = "22.53538, 22.535363, 22.53513, 22.534729, 22.533611, 22.532743, 22.531675, 22.530573, 22.530473, 22.53049, 22.530873, 22.531741, 22.532042, 22.532225, 22.532459, 22.532409, 22.527335, 22.527468, 22.527652, 22.528537, 22.530289, 22.531958, 22.531291, 22.53104, 22.530656, 22.532659, 22.533461, 22.534662, 22.536899, 22.5376, 22.53795, 22.538151, 22.538134";
		String lo = "113.951973, 113.950859, 113.949781, 113.948865, 113.947518, 113.946476, 113.945757, 113.94538, 113.947194, 113.948452, 113.949207, 113.949835, 113.950249, 113.950644, 113.951345, 113.952081, 113.952225, 113.943655, 113.939577, 113.936253, 113.93593, 113.935858, 113.937169, 113.938139, 113.94184, 113.942936, 113.943529, 113.944841, 113.947554, 113.948811, 113.949763, 113.950859, 113.951973";

		String[] las = la.split(",");
		Double[] dlas = new Double[las.length];
		for (int i = 0; i < las.length; i++) {
			dlas[i] = Double.valueOf(Double.parseDouble(las[i]));
		}
		String[] los = lo.split(",");
		Double[] dlos = new Double[los.length];
		for (int i = 0; i < los.length; i++) {
			dlos[i] = Double.valueOf(Double.parseDouble(los[i]));
		}
		System.out.println(doJudge(dlas, dlos, 22.529012000000002D, 113.948127D));
	}

	public static Boolean doJudge(Double[] cornerx, Double[] cornery, double x, double y) {
		return Boolean.valueOf(true);
	}
}