package project;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Assembler {

	public static String assemble(File input, File output) {
		String ret = "success";
		ArrayList<String> inText = new ArrayList<>();
		ArrayList<String> code = new ArrayList<>();
		ArrayList<String> data = new ArrayList<>();
		boolean incode = true;
		boolean error = false;
		try (Scanner inp = new Scanner(input)) {
			while(inp.hasNextLine()) {
				inText.add(inp.nextLine());
//				String line = inp.nextLine(); 
//				if(incode && line.trim().equals("DATA")) incode = false;
//				else if(incode) code.add(line);
//				else data.add(line);
			}
		} catch (FileNotFoundException e) {
			ret = "Error: the source file " + input + " is missing";
		}
		A : for (int lineNum = 0; lineNum < inText.size(); lineNum++) {
			String line = inText.get(lineNum);
			if (line.trim().length() == 0 ) {
				for (int backupLine = lineNum;  backupLine < inText.size(); backupLine++) {
					if (inText.get(backupLine).trim().length() != 0) {
						ret = "Error: line " + (lineNum+1) + " is a blank line";
						break A;
					}
				}
			} else if (line.charAt(0) == ' ' || line.charAt(0) == '\t') {
				ret = "Error: line " + (lineNum+1) + " starts with white space";
				break;
			} else if (incode && line.trim().toUpperCase().equals("DATA")) {
				if (!line.trim().equals("DATA")) {
					ret = "Error: line " + (lineNum+1) + " does not have DATA in upper case";
					break;
				} 
				else {
					incode = false;
				}
			} 
			else if (incode) {
				code.add(inText.get(lineNum));
			} else {
				data.add(inText.get(lineNum));
			}
		}
//		for (int lineNum = 0; lineNum < inText.size(); lineNum++) {
//			String line = inText.get(lineNum);
//			System.out.println(line + " " + (lineNum + 1));
//			if (line.trim().length() == 0 && inText.get(lineNum+1).trim().length() != 0) {
//				ret = "Error: line " + (lineNum + 1) + " is a blank line";
//				break;
//			} else if (line.charAt(0) == ' ' || line.charAt(0) == '\t') {
//				ret = "Error: line " + (lineNum + 1) + " starts with white space";
//				break;
//			} else if (incode && line.trim().toUpperCase().equals("DATA")) {
//				if (!line.trim().equals("DATA")) {
//					ret = "Error: line " + (lineNum + 1) + " does not have DATA in upper case";
//				} else {
//					incode = false;
//				}
//			} else if (incode) {
//				code.add(line);
//			} else {
//				data.add(line);
//			}
//		}

		ArrayList<String> outText = new ArrayList<>();
		int lineNum = 0;
		for(String s : code) {
			String[] parts = s.split("\\s+");
			//checks if instruction is uppercase
			if (InstructionMap.sourceCodes.contains(parts[0].toUpperCase())) {
				if (!InstructionMap.sourceCodes.contains(parts[0])) {
					ret = "Error: line " + (lineNum+1) + " does not have the instruction mnemonic in upper case";
					error = true;
					break;
				} else if (InstructionMap.noArgument.contains(parts[0])) {
					if (parts.length != 1) {
						ret = "Error: line " + (lineNum + 1) + " has an illegal argument";
						error = true;
						break;
					} else {
						int opcode = InstructionMap.opcode.get(parts[0]);
						outText.add(Integer.toHexString(opcode).toUpperCase() 
								+ " " + 0);
					}
				} else if (!InstructionMap.noArgument.contains(parts[0])) {
					if (parts.length == 1) {
						ret = "Error: line " + (lineNum + 1) + " is missing an argument";
						error = true;
						break;
					} else if (parts.length > 2) {
						ret = "Error: line " + (lineNum + 1) + " has more than one argument";
						error = true;
						break;
					} else {
						if (parts[1].startsWith("#")) {
							if (InstructionMap.immediateOK.contains(parts[0])) {
								parts[0] = parts[0] + "I";
								parts[1] = parts[1].substring(1);
								if(parts[0].equals("JUMPI")) parts[0] = "JMPI"; 
								if(parts[0].equals("JMPZI")) parts[0] = "JMZI"; 
							} else {
								ret = "Error: line " + (lineNum+1) + " instruction cannot be immediate";
								error = true;
								break;
							}
						} else if (parts[1].startsWith("&")) {
							if (InstructionMap.indirectOK.contains(parts[0])) {
								parts[0] = parts[0] + "N";
								parts[1] = parts[1].substring(1);
								if(parts[0].equals("JUMPN")) parts[0] = "JMPN"; 
							} else {
								ret = "Error: line " + (lineNum + 1) + " instruction cannot be indirect";
								error = true;
								break;
							}
						}
						int opcode = InstructionMap.opcode.get(parts[0]);
						outText.add(Integer.toHexString(opcode).toUpperCase() 
								+ " " + parts[1]);
						 
						try {
							Integer.parseInt(parts[1],16); //<<<<< CORRECTION
						} catch (NumberFormatException e) {
							ret = "Error: line " + (lineNum + 1)
								+ " does not have a numeric argument";
							error = true;
							break;
						}
					}
				}
			} else {
				ret = "Error: line " + (lineNum + 1) + " does not have a correct instruction mnemonic";
				error = true;
				break;
			} 
			lineNum++;
		}
		lineNum++;
		
		if (!error) {
			for (String d : data) {
				String[] parts = d.split("\\s+");
				System.out.println(parts[0] + " " + parts[1]);
				if (parts.length == 2) {
					try {
						Integer.parseInt(parts[0],16); //<<<<< CORRECTION
					} catch (NumberFormatException e) {
						ret = "Error: line " + (lineNum + 1)
							+ " does not have a numeric address";
						break;
					}
					try {
						Integer.parseInt(parts[1],16); //<<<<< CORRECTION
					} catch (NumberFormatException e) {
						ret = "Error: line " + (lineNum + 1)
							+ " does not have a numeric value";
						break;
					}
				} else if (parts.length > 2) {
					System.out.println("length is " + parts.length);
					ret = "Error: line " + (lineNum + 1) + " has invalid data";
					break;
				}
				lineNum++;
			}
		}
		outText.add("-1");
		outText.addAll(data);

		if(ret.equals("success")) {
			try (PrintWriter outp = new PrintWriter(output)){
				for(String str : outText) {
					outp.println(str);
				}
				outp.close();
			} catch (FileNotFoundException e) {
				ret = "Error: unable to open " + output; 
			}

		}
		return ret;

	}

	public static void main(String[] args) {
		System.out.println("Enter the name of the file without extension: ");
		try (Scanner keyboard = new Scanner(System.in)) {
			String filename = keyboard.nextLine();
			String i = assemble(new File(filename + ".pasm"), 
					new File(filename + ".pexe"));
			System.out.println(i );
		}
	}

}
