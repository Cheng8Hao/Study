package com.dyc.factorymode.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class ShellExe {
	private static final String TAG = "EM/shellexe";
	public static final String ERROR = "ERROR";
	public static final int RESULT_SUCCESS = 0;
	public static final int RESULT_FAIL = -1;
	public static final int RESULT_EXCEPTION = -2;
	private static StringBuilder sResultBuilder = new StringBuilder("");

	public static String getOutput() {
		return sResultBuilder.toString();
	}

	public static int execCommand(String command) throws IOException {
		return execCommand(new String[] { "sh", "-c", command });
	}

	public static int execCommand(String[] command) throws IOException {
		int result = RESULT_FAIL;
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(command);
		BufferedReader bufferedReader = null;
		sResultBuilder.delete(0, sResultBuilder.length());
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream(), Charset.defaultCharset()));
			if (proc.waitFor() == 0) {
				String line = bufferedReader.readLine();
				if (line != null) {
					sResultBuilder.append(line);
					while (true) {
						line = bufferedReader.readLine();
						if (line == null) {
							break;
						} else {
							sResultBuilder.append('\n');
							sResultBuilder.append(line);
						}
					}
				}
				result = RESULT_SUCCESS;
			} else {
				sResultBuilder.append(ERROR);
				result = RESULT_FAIL;
			}
		} catch (InterruptedException e) {
			sResultBuilder.append(ERROR);
			result = RESULT_EXCEPTION;
		} finally {
			if (null != bufferedReader) {
				try {
					bufferedReader.close();
				} catch (IOException e) {}
			}
		}
		return result;
	}
}
