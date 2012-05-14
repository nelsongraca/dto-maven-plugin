/**
 *
 */
package fr.maven.dto.generator.impl;

import fr.maven.dto.generator.DTOGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * {@link DTOGenerator} implementation.
 *
 * @author Wilfried Petit
 */
public class DTOGeneratorImpl implements DTOGenerator {

    /**
     * File writes associated to classes.
     */
    protected final Map<Class<?>, FileWriter> fileWriters;

    /**
     * The classes list we want to generate for.
     */
    protected List<Class<?>> classesToGenerate;

    /**
     * The directory where DTOs will be generated.
     */
    protected File generatedDirectory;

    private String generatedPackage;
    private Set<String> additionalImports = new TreeSet<String>();

    /**
     * Constructor.
     */
    public DTOGeneratorImpl() {
        this.classesToGenerate = new ArrayList<Class<?>>();
        this.fileWriters = new HashMap<Class<?>, FileWriter>();
    }

    /**
     * Set the DTOs generated directory.
     *
     * @param generatedDirectory the DTOs generated directory to set
     */
    @Override
    public void setGeneratedDirectory(final File generatedDirectory) {
        this.generatedDirectory = generatedDirectory;
    }

    @Override
    public void setGeneratedPackage(String generatedPackage) {
        this.generatedPackage = generatedPackage;
    }

    /**
     * {@inheritDoc}
     *
     * @see fr.maven.dto.generator.DTOGenerator#generateDTOs(java.util.List)
     */
    @Override
    public void generateDTOs(final List<Class<?>> classes) throws IOException {
        this.classesToGenerate = classes;
        for (final Class<?> clazz : classes) {
            this.generateDTO(clazz);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see fr.maven.dto.generator.DTOGenerator#generateDTO(java.lang.Class)
     */
    @Override
    public void generateDTO(final Class<?> clazz) throws IOException {
        this.makeDTOPackage(clazz);
        if (clazz.isEnum()) {
            final FileWriter fw = this.getDTOClassFileWriter(clazz);

            this.makeDTOEnum(clazz, fw);
        }
        else {
            final FileWriter fw = this.getDTOClassFileWriter(clazz);
            this.makeDTOClass(clazz, fw);
        }
        this.getDTOClassFileWriter(clazz).close();
    }

    /**
     * Create the directory of the class package.
     *
     * @param clazz the class we want to create the package directory for.
     * @throws IOException if the directory creation failed.
     */
    protected void makeDTOPackage(final Class<?> clazz) throws IOException {
        final File packageDirectory = new File(
                this.generatedDirectory.getAbsolutePath()
                        + File.separator
                        + this.getDTOPackage(clazz).replace('.',
                        File.separatorChar));
        if (!packageDirectory.exists()) {
            final boolean directoryCreated = packageDirectory.mkdirs();
            if (!directoryCreated) {
                throw new IOException(
                        "The generated directory can not be created.");
            }
        }
    }

    /**
     * Return the package name of the DTO for the clazz given.
     *
     * @param clazz the class we want to generate DTO for.
     * @return the package name got.
     */
    protected String getDTOPackage(final Class<?> clazz) {
//        return clazz.getPackage().getName() + ".dto";
        return this.generatedPackage;
    }

    /**
     * Check if the class belongs to the classes we want to generate DTO for.
     *
     * @param clazz the class to check.
     * @return <code>true</code> if the classes list contains the class.
     */
    protected boolean isClassToGenerate(final Class<?> clazz) {
        return this.classesToGenerate.contains(clazz);
    }

    /**
     * Return the type of the DTO field class that belongs to the class given.
     *
     * @param clazz the class that contains the field.
     * @param field the field we want to know its DTO type.
     * @return the type.
     */
    protected String getDTOFieldType(final Class<?> clazz, final Field field) {
        return this.getDTOType(clazz, field.getGenericType());
    }

    /**
     * Return the type canonical name for the field type given.
     *
     * @param clazz the class that contains the field.
     * @param type  the field type.
     * @return the type canonical name.
     */
    protected String getDTOType(final Class<?> clazz, final Type type) {
        final StringBuffer typeSimpleName = new StringBuffer();
        typeSimpleName.append(this.getDTOFieldPackage(clazz, type));

        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            typeSimpleName.append(((Class<?>) parameterizedType.getRawType()).getSimpleName() + "<");
            final Type[] typeArguments = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < typeArguments.length; i++) {
                typeSimpleName.append(this.getDTOType(clazz, typeArguments[i]));
                if (i != (typeArguments.length - 1)) {
                    typeSimpleName.append(", ");
                }
            }
            typeSimpleName.append(">");
        }
        else {
            final Class<?> clazzType = ((Class<?>) type);
            if (clazzType.isArray()) {
                typeSimpleName.append(this.getArrayComponentType(clazzType).getSimpleName());
                for (int i = 0; i < this.getArrayDimension(clazzType); i++) {
                    typeSimpleName.append("[]");
                }
            }
            else {
                typeSimpleName.append(((Class<?>) type).getSimpleName());
                if (this.isClassToGenerate(clazzType)) {
                    typeSimpleName.append("DTO");
                }
            }
        }
        return typeSimpleName.toString();
    }

    /**
     * Return the real component type of an array. E.g. for Bean[], Bean[][]
     * ,... , it returns Bean
     *
     * @param clazz the array
     * @return the component type
     */
    protected Class<?> getArrayComponentType(final Class<?> clazz) {
        if (clazz.isArray()) {
            return this.getArrayComponentType(clazz.getComponentType());
        }
        else {
            return clazz;
        }
    }

    /**
     * Return the dimension of the array. E.g. : For Bean[] it returns 1. For
     * Bean[][] it returns 2...
     *
     * @param clazz the array we want the dimension.
     * @return the dimension.
     */
    protected int getArrayDimension(final Class<?> clazz) {
        int arrayDimension = 0;
        if (clazz.isArray()) {
            arrayDimension++;
            arrayDimension += this.getArrayDimension(clazz.getComponentType());
        }
        return arrayDimension;
    }

    /**
     * Return the DTO field package class that belongs to the class given.
     *
     * @param clazz     the class that contains the field.
     * @param fieldType the field type we want to know its DTO package.
     * @return the type.
     */
    protected String getDTOFieldPackage(final Class<?> clazz, final Type fieldType) {
        Class<?> fieldTypeClass;
        Package fieldPackage;
        if (fieldType instanceof ParameterizedType) {
            fieldTypeClass = ((Class<?>) ((ParameterizedType) fieldType).getRawType());
        }
        else {
            fieldTypeClass = (Class<?>) fieldType;
            if (fieldTypeClass.isArray()) {
                fieldTypeClass = this.getArrayComponentType(fieldTypeClass);
            }
        }
        fieldPackage = ((Class<?>) fieldTypeClass).getPackage();
        String result = "";
        if (fieldPackage != null && !"java.lang".equals(fieldPackage.getName())) {
            if (this.isClassToGenerate(fieldTypeClass)) {
                if (!clazz.getPackage().getName().equals(fieldTypeClass.getPackage().getName())) {
                    result = this.getDTOPackage(fieldTypeClass) + ".";
                }
            }
            else if (fieldTypeClass.getDeclaringClass() != null) {
                result = "";
            }
            else {
                result = fieldTypeClass.getPackage().getName() + ".";
            }
        }
        return result;
    }

    /**
     * Return the file writer used to write the DTO for the class given.
     *
     * @param clazz the class we want to generate a DTO for.
     * @return the file write associated to the DTO.
     * @throws IOException if the file is not writable.
     */
    protected FileWriter getDTOClassFileWriter(final Class<?> clazz) throws IOException {
        if (!this.fileWriters.containsKey(clazz)) {
            final String directory = this.generatedDirectory.getAbsolutePath() + File.separator + this.getDTOPackage(clazz).replace(".", File.separator);
            final File classFile = new File(directory.concat(File.separator).concat(clazz.getSimpleName()).concat("DTO.java"));
            if (classFile.exists()) {
                final boolean fileDeleted = classFile.delete();
                if (fileDeleted) {
                    final boolean fileCreated = classFile.createNewFile();
                    if (!fileCreated) {
                        throw new IOException("The file " + classFile + " can not be created.");
                    }
                }
            }
            final FileWriter fw = new FileWriter(classFile);
            this.fileWriters.put(clazz, fw);
            return fw;
        }
        else {
            return this.fileWriters.get(clazz);
        }
    }


    protected void makeDTOClass(final Class<?> clazz, FileWriter fw) throws IOException {
        makeDTOClass(clazz, fw, "DTO", true);
    }


    /**
     * Write class part in the DTO generation file.
     *
     * @param clazz the clazz we want a DTO for.
     * @throws IOException if the file is not writable.
     */

    protected void makeDTOClass(final Class<?> clazz, FileWriter fw, String nameToAppend, boolean writeHeader) throws IOException {
        if (writeHeader) {
            fw.write("package " + this.getDTOPackage(clazz) + ";" + "\n\n");
            fw.write("import java.io.Serializable;\n\n");
        }
        //make params into string buffer so we can have the data types for additional imports
        StringBuffer stringBuffer = new StringBuffer();
        for (final Field field : getDeclaredFields(clazz)) {
            if (!Modifier.isStatic(field.getModifiers())) {
                stringBuffer.append(this.makeDTOField(clazz, field));
            }
        }
        if (writeHeader) {
            for (String pack : additionalImports) {
                fw.write("import " + pack + ";\n");
            }
        }

        fw.write("/**\n");
        fw.write(" * This class was generated by the DTO Maven Plugin.\n");
        fw.write(" * " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()) + "\n");
        fw.write(" */\n");
        fw.write(this.getClassModifiers(clazz) + "class " + clazz.getSimpleName() + nameToAppend + " implements Serializable {\n\n");
        fw.write("    private static final long serialVersionUID = 1L;\n\n");

        //now write SB
        fw.write(stringBuffer.toString());


        //make constructor with all parameters
        fw.write("    public " + clazz.getSimpleName() + nameToAppend + "(");
        boolean first = true;
        for (final Field field : getDeclaredFields(clazz)) {
            if (!Modifier.isStatic(field.getModifiers())) {
                if (first) {
                    first = false;
                }
                else {
                    fw.write(", ");
                }
                fw.write(this.getDTOFieldType(clazz, field) + " " + field.getName());
            }
        }

        fw.write(") {\n");
        for (final Field field : getDeclaredFields(clazz)) {
            if (!Modifier.isStatic(field.getModifiers())) {
                fw.write("        this." + field.getName() + " = " + field.getName() + ";\n");
            }
        }
        fw.write("    }\n\n");

        for (final Field field : getDeclaredFields(clazz)) {
            if (!Modifier.isStatic(field.getModifiers())) {
                this.makeDTOFieldGetter(clazz, field, fw);
                this.makeDTOFieldSetter(clazz, field, fw);
            }
        }

        Class<?>[] clazzes = clazz.getDeclaredClasses();
        for (Class<?> clazze : clazzes) {
            if (clazze.isEnum()) {
                makeDTOEnum(clazze, fw, "", false);
            }
            else {
                makeDTOClass(clazze, fw, "", false);
            }
        }
        fw.write("}");
    }

    private String getClassModifiers(Class<?> clazz) {
        int modifiers = clazz.getModifiers();
        StringBuilder stringBuilder = new StringBuilder();

        if (Modifier.isPublic(modifiers)) {stringBuilder.append("public ");}
        if (Modifier.isProtected(modifiers)) {stringBuilder.append("protected ");}
        if (Modifier.isPrivate(modifiers)) {stringBuilder.append("private ");}
        if (Modifier.isFinal(modifiers) && !clazz.isEnum()) {stringBuilder.append("final ");}
        if (Modifier.isInterface(modifiers)) {stringBuilder.append("interface ");}
        if (Modifier.isNative(modifiers)) {stringBuilder.append("native ");}
        if (Modifier.isStatic(modifiers)) {stringBuilder.append("static ");}
        if (Modifier.isStrict(modifiers)) {stringBuilder.append("strict ");}
        if (Modifier.isSynchronized(modifiers)) {stringBuilder.append("synchronized ");}
        if (Modifier.isTransient(modifiers)) {stringBuilder.append("transient ");}
        if (Modifier.isVolatile(modifiers)) {stringBuilder.append("volatile ");}

        return stringBuilder.toString();
    }


    private List<Field> getDeclaredFields(Class<?> clazz) {

        Field[] declaredFields = clazz.getDeclaredFields();
        List<Field> ret = new ArrayList<Field>();
        for (Field declaredField : declaredFields) {
            if (declaredField.getDeclaringClass().getDeclaringClass() != null) {
                if (!declaredField.getDeclaringClass().getDeclaringClass().equals(declaredField.getType())) {
                    ret.add(declaredField);
                }
            }
            else {
                ret.add(declaredField);
            }

        }
        return ret;
    }

    protected void makeDTOEnum(final Class<?> clazz, FileWriter fw) throws IOException {
        makeDTOEnum(clazz, fw, "DTO", true);
    }

    /**
     * Write class part in the DTO generation file.
     *
     * @param clazz the clazz we want a DTO for.
     * @throws IOException if the file is not writable.
     */
    protected void makeDTOEnum(final Class<?> clazz, FileWriter fw, String nameToAppend, boolean writeHeader) throws IOException {
        if (writeHeader) {
            fw.write("package " + this.getDTOPackage(clazz) + ";" + "\n\n");
            fw.write("import java.io.Serializable;\n\n");
        }
        fw.write("/**\n");
        fw.write(" * This class was generated by the DTO Maven Plugin.\n");
        fw.write(" * " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()) + "\n");
        fw.write(" */\n");
        fw.write(getClassModifiers(clazz) + "enum " + clazz.getSimpleName() + nameToAppend + " implements Serializable {\n\n");


        for (Object o : clazz.getEnumConstants()) {
            fw.write("    " + o.toString() + ",\n");
        }
        fw.write("\n}");
    }

    /**
     * Write field part in the DTO generation file.
     *
     * @param clazz the clazz we want a DTO for.
     * @param field the field we want to write the part for.
     * @return String
     * @throws IOException if the file is not writable.
     */
    protected String makeDTOField(final Class<?> clazz, final Field field) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        final String fieldType = this.getDTOFieldType(clazz, field);
        stringBuffer.append("    /**\n");
        stringBuffer.append("     * @see " + clazz.getCanonicalName() + "#" + field.getName() + "\n");
        stringBuffer.append("     */\n");
        stringBuffer.append("    private " + fieldType + " " + field.getName() + ";\n\n");
        return stringBuffer.toString();
    }

    /**
     * Write field getter part in the DTO generation file.
     *
     * @param clazz the clazz we want a DTO for.
     * @param field the field we want to write the part for.
     * @throws IOException if the file is not writable.
     */
    protected void makeDTOFieldGetter(final Class<?> clazz, final Field field, FileWriter fw)
            throws IOException {
        String methodSignature;
        final char firstFieldNameCharacterUpper = Character.toUpperCase(field.getName().charAt(0));
        String fieldNameWithoutFirstCharacter = "";
        if (field.getName().length() > 1) {
            fieldNameWithoutFirstCharacter = field.getName().substring(1);
        }
        if (field.getType().equals(boolean.class)) {
            methodSignature = "is" + firstFieldNameCharacterUpper + fieldNameWithoutFirstCharacter;
        }
        else {

            methodSignature = "get" + firstFieldNameCharacterUpper + fieldNameWithoutFirstCharacter;
        }
        fw.write("    /**\n");
        fw.write("     * @see " + clazz.getCanonicalName() + "#" + methodSignature + "()\n");
        fw.write("     */\n");
        fw.write("    public " + this.getDTOFieldType(clazz, field) + " "
                + methodSignature + "() {\n");
        fw.write("        return this." + field.getName() + ";\n");
        fw.write("    }\n\n");
    }

    /**
     * Write field setter part in the DTO generation file.
     *
     * @param clazz the clazz we want a DTO for.
     * @param field the field we want to write the part for.
     * @throws IOException if the file is not writable.
     */
    protected void makeDTOFieldSetter(final Class<?> clazz, final Field field, FileWriter fw) throws IOException {

        final char firstFieldNameCharacterUpper = Character.toUpperCase(field.getName().charAt(0));
        String fieldNameWithoutFirstCharacter = "";
        if (field.getName().length() > 1) {
            fieldNameWithoutFirstCharacter = field.getName().substring(1);
        }
        final String methodSignature = "set" + firstFieldNameCharacterUpper + fieldNameWithoutFirstCharacter;
        fw.write("    /**\n");
        fw.write("     * @see " + clazz.getCanonicalName() + "#" + methodSignature + "(" + field.getType().getSimpleName() + ")\n");
        fw.write("     */\n");


        fw.write("    public void " + methodSignature + "(" + this.getDTOFieldType(clazz, field) + " " + field.getName() + ") {\n");
        fw.write("        this." + field.getName() + " = " + field.getName() + ";\n");
        fw.write("    }\n\n");
    }
}
