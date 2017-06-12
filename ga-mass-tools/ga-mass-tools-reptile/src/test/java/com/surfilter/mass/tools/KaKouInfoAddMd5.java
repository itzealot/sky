package com.surfilter.mass.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.surfilter.mass.tools.util.Closeables;
import com.surfilter.mass.tools.util.EncryptUtils;
import com.surfilter.mass.tools.util.FileUtil;

public class KaKouInfoAddMd5 {

	public static void main(String[] args) {
		String path = "C:/Users/Administrator/Desktop/fxj_licheng/kakou.txt";
		int size = 500;

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(path));
			String line = null;
			List<String> lines = new ArrayList<>(size);

			StringBuffer buffer = new StringBuffer(512);
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}

				String[] arrays = line.split("\t");
				buffer.append(arrays[0]);
				buffer.append("\t");
				buffer.append(arrays[1]);
				buffer.append("\t");
				buffer.append(EncryptUtils.getMD5Str16(arrays[1]));
				buffer.append("\t");
				for (int i = 2; i < arrays.length; i++) {
					buffer.append(arrays[i]);
					buffer.append("\t");
				}

				lines.add(buffer.deleteCharAt(buffer.length() - 1).toString());
				buffer.setLength(0);

				if (lines.size() >= size) {
					FileUtil.append(new File("C:/Users/Administrator/Desktop/fxj_licheng/kakou_md5.txt"), lines);
					lines.clear();
				}
			}

			if (lines.size() > 0) {
				FileUtil.append(new File("C:/Users/Administrator/Desktop/fxj_licheng/kakou_md5.txt"), lines);
				lines.clear();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Closeables.close(reader);
		}
	}

}
