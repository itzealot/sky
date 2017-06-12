package com.surfilter.mass.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.surfilter.mass.tools.util.Closeables;
import com.surfilter.mass.tools.util.Threads;

public class FileReadAndWriteTest {

	public static void main(String[] args) {
		String file = "E:/test/test.txt";

		new Thread(new Runnable() {
			@Override
			public void run() {
				int counts = 0;
				while (true) {
					List<String> lines = new ArrayList<>(10);

					for (int i = 0; i < 10; i++) {
						lines.add("int:" + new Random().nextInt());
					}

					write(file, lines);
					counts += lines.size();
					System.out.println("write size:" + lines.size() + ", total:" + counts);
					lines.clear();
					lines = null;
					Threads.sleep(1000);
				}
			}
		}).start();

		Threads.sleep(1000);

		new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(file));
					String line = null;
					int counts = 0;

					while ((line = reader.readLine()) != null) {
						counts++;
						System.out.println("line:" + line + ", total:" + counts);
						Threads.sleep(200);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Closeables.close(reader);
				}
			}
		}).start();
	}

	public static void write(String file, List<String> lines) {
		BufferedWriter writer = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file, true);
			writer = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("UTF-8")));

			for (String line : lines) {
				writer.write(line);
				writer.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Closeables.close(writer, fos);
		}
	}
}
