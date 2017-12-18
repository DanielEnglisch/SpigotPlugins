package at.jellybit.templatebuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Builder {

	public static void main(String[] args) throws Exception {

		if(args.length != 2)
			return;
		
		new Builder().create(args[0], args[1]);

	}

	private void create(String name, String commands) throws Exception {
		File templ_config = new File(getClass().getClassLoader().getResource("info.yml").getFile());
		File temp_main = new File(getClass().getClassLoader().getResource("Main.txt").getFile());

		String pkg = "at.jellybit.plugin";
		String main_class = "Main";
		String author = "Daniel Englisch";
		String version = "1.0";
		String description = "";

		File pack = new File(name + "/" + pkg.replaceAll("\\.", "/"));
		pack.mkdirs();

		File config = new File(name + "/plugin.yml");
		File mainFile = new File(pack, main_class + ".java");

		config.createNewFile();
		mainFile.createNewFile();

		BufferedReader in = new BufferedReader(new FileReader(templ_config));
		BufferedWriter out = new BufferedWriter(new FileWriter(config));

		while (in.ready()) {
			String line = in.readLine();
			line = line.replaceAll("\\{NAME\\}", name).replaceAll("\\{DESCRIPTION\\}", description)
					.replaceAll("\\{PACKAGE\\}", pkg).replaceAll("\\{MAIN_CLASS\\}", main_class)
					.replaceAll("\\{VERSION\\}", version).replaceAll("\\{AUTHOR\\}", author)
					.replaceAll("\\{COMMANDS\\}", commands);

			out.write(line + "\n");
		}

		out.flush();
		out.close();
		in.close();
		
		
		in = new BufferedReader(new FileReader(temp_main));
		out = new BufferedWriter(new FileWriter(mainFile));

		while (in.ready()) {
			String line = in.readLine();
			line = line.replaceAll("\\{NAME\\}", name).replaceAll("\\{DESCRIPTION\\}", description)
					.replaceAll("\\{PACKAGE\\}", pkg).replaceAll("\\{MAIN_CLASS\\}", main_class)
					.replaceAll("\\{VERSION\\}", version).replaceAll("\\{AUTHOR\\}", author)
					.replaceAll("\\{COMMANDS\\}", commands);

			out.write(line + "\n");
		}

		out.flush();
		out.close();
		in.close();
		
		System.out.println("Done creating structure for plugin " + name);
	}

}
