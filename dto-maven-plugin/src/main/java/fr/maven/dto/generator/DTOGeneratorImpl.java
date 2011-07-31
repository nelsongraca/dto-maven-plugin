/**
 * 
 */
package fr.maven.dto.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wilfried Petit
 * 
 */
public class DTOGeneratorImpl implements DTOGenerator {

	protected final Map<Class<?>, FileWriter> fileWriters;
	protected List<Class<?>> classesToGenerate;
	protected File generatedDirectory;

	/**
	 * Constructor.
	 */
	public DTOGeneratorImpl() {
		this.classesToGenerate = new ArrayList<Class<?>>();
		this.fileWriters = new HashMap<Class<?>, FileWriter>();
	}

	/**
	 * @param generatedDirectory
	 *            the generatedDirectory to set
	 */
	@Override
	public void setGeneratedDirectory(File generatedDirectory) {
		this.generatedDirectory = generatedDirectory;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 * 
	 * @see fr.maven.dto.generator.DTOGenerator#generateDTOs(java.util.List)
	 */
	@Override
	public void generateDTOs(List<Class<?>> classes) throws IOException {
		this.classesToGenerate = classes;
		for (Class<?> clazz : classes) {
			this.generateDTO(clazz);
		}
	}

	@Override
	public void generateDTO(Class<?> clazz) throws IOException {
		this.makeDTOPackage(clazz);
		this.makeDTOClass(clazz);
		this.getDTOClassFileWriter(clazz).close();
	}

	protected void makeDTOPackage(Class<?> clazz) throws IOException {
		File packageDirectory = new File(
				this.generatedDirectory.getAbsolutePath()
						+ File.separator
						+ this.getDTOPackage(clazz).replace('.',
								File.separatorChar));
		if (!packageDirectory.exists()) {
			boolean directoryCreated = packageDirectory.mkdirs();
			if (!directoryCreated) {
				throw new IOException(
						"The generated directory can not be created.");
			}
		}
	}

	protected String getDTOPackage(Class<?> clazz) {
		return clazz.getPackage().getName() + ".dto";
	}

	protected boolean isClassToGenerate(Class<?> clazz) {
		return this.classesToGenerate.contains(clazz);
	}

	protected String getDTOFieldType(Class<?> clazz, Field field) {
		String fieldType = field.getType().getSimpleName();
		if (this.isClassToGenerate(field.getType())) {
			fieldType += "DTO";
		}
		if (field.getType().isArray()) {
			fieldType += "[]";
		}
		return fieldType;
	}

	protected String getDTOFieldPackage(Class<?> clazz, Field field) {
		Package fieldPackage = field.getType().getPackage();
		String result = "";
		if (fieldPackage != null && !"java.lang".equals(fieldPackage.getName())) {
			if (this.isClassToGenerate(field.getType())) {
				if (!clazz.getPackage().getName()
						.equals(field.getType().getPackage().getName())) {
					result = this.getDTOPackage(field.getType()) + ".";
				}
			} else {
				result = field.getType().getPackage().getName() + ".";
			}
		}
		return result;
	}

	protected FileWriter getDTOClassFileWriter(Class<?> clazz)
			throws IOException {
		if (!this.fileWriters.containsKey(clazz)) {
			String directory = this.generatedDirectory.getAbsolutePath()
					+ File.separator
					+ this.getDTOPackage(clazz).replace(".", File.separator);
			File classFile = new File(directory.concat(File.separator)
					.concat(clazz.getSimpleName()).concat("DTO.java"));
			if (classFile.exists()) {
				classFile.delete();
			}
			boolean fileCreated = classFile.createNewFile();
			if (!fileCreated) {
				throw new IOException("The file " + classFile
						+ " can not be created.");
			}
			FileWriter fw = new FileWriter(classFile);
			this.fileWriters.put(clazz, fw);
			return fw;
		} else {
			return this.fileWriters.get(clazz);
		}
	}

	protected void makeDTOClass(Class<?> clazz) throws IOException {
		FileWriter fw = this.getDTOClassFileWriter(clazz);
		fw.write("package " + this.getDTOPackage(clazz) + ";" + "\n\n");
		fw.write("import java.io.Serializable;\n\n");

		/**
		 * This class was generated by Apache CXF 2.4.1
		 * 2011-07-19T22:38:12.464+02:00 Generated source version: 2.4.1
		 * 
		 */

		fw.write("/**\n");
		fw.write(" * This class was generated by the DTO Maven Plugin.\n");
		fw.write(" * "
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())
				+ "\n");
		fw.write(" */\n");
		fw.write("public class " + clazz.getSimpleName()
				+ "DTO implements Serializable {\n\n");
		fw.write("\tprivate static final long serialVersionUID = 1L;\n\n");
		for (Field field : clazz.getDeclaredFields()) {
			this.makeDTOField(clazz, field);
		}
		for (Field field : clazz.getDeclaredFields()) {
			this.makeDTOFieldGetter(clazz, field);
			this.makeDTOFieldSetter(clazz, field);
		}
		fw.write("}");
	}

	protected void makeDTOField(Class<?> clazz, Field field) throws IOException {
		FileWriter fw = this.getDTOClassFileWriter(clazz);
		String fieldPackage = this.getDTOFieldPackage(clazz, field);
		String fieldType = this.getDTOFieldType(clazz, field);
		fw.write("\t/**\n");
		fw.write("\t * @see " + clazz.getCanonicalName() + "#"
				+ field.getName() + "\n");
		fw.write("\t */\n");
		fw.write("\tprivate " + fieldPackage + fieldType + " "
				+ field.getName() + ";\n\n");

	}

	protected void makeDTOFieldGetter(Class<?> clazz, Field field)
			throws IOException {
		FileWriter fw = this.getDTOClassFileWriter(clazz);
		String methodSignature;
		char firstCharacter = field.getName().charAt(0);
		if (field.getType().equals(boolean.class)) {
			methodSignature = "is"
					+ field.getName().replace(field.getName().charAt(0),
							Character.toUpperCase(firstCharacter));
		} else {
			methodSignature = "get"
					+ field.getName().replace(field.getName().charAt(0),
							Character.toUpperCase(firstCharacter));
		}
		fw.write("\t/**\n");
		fw.write("\t * @see " + clazz.getCanonicalName() + "#"
				+ methodSignature + "()\n");
		fw.write("\t */\n");
		fw.write("\tpublic " + this.getDTOFieldPackage(clazz, field)
				+ this.getDTOFieldType(clazz, field) + " " + methodSignature
				+ "() {\n");
		fw.write("\t\treturn this." + field.getName() + ";\n");
		fw.write("\t}\n\n");
	}

	protected void makeDTOFieldSetter(Class<?> clazz, Field field)
			throws IOException {
		FileWriter fw = this.getDTOClassFileWriter(clazz);
		char firstCharacter = field.getName().charAt(0);
		String methodSignature = "set"
				+ field.getName().replace(field.getName().charAt(0),
						Character.toUpperCase(firstCharacter));
		fw.write("\t/**\n");
		fw.write("\t * @see " + clazz.getCanonicalName() + "#"
				+ methodSignature + "(" + field.getType().getSimpleName()
				+ ")\n");
		fw.write("\t */\n");
		fw.write("\tpublic void " + methodSignature + "("
				+ this.getDTOFieldPackage(clazz, field)
				+ this.getDTOFieldType(clazz, field) + " " + field.getName()
				+ ") {\n");
		fw.write("\t\tthis." + field.getName() + " = " + field.getName()
				+ ";\n");
		fw.write("\t}\n\n");
	}
}
