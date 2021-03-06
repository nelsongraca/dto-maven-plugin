/**
 *
 */
package fr.maven.dto.generator.impl;

import fr.maven.dto.bean.AnotherBean;
import fr.maven.dto.bean.Bean;
import fr.maven.dto.bean.Bean2;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests on {@link DTOGeneratorImpl}.
 *
 * @author Wilfried Petit
 */
public class DTOGeneratorImplTest {

    private DTOGeneratorImpl dtoGeneratorImpl;
    private File generatedDirectory;
    private String generatedPackage;

    /**
     * Set up configuration for generation.
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.dtoGeneratorImpl = new DTOGeneratorImpl();
        this.generatedDirectory = new File("target" + File.separator + "generated");
        this.generatedPackage = "fr.maven.dto.bean.dto";
        this.dtoGeneratorImpl.setGeneratedDirectory(this.generatedDirectory);
        this.dtoGeneratorImpl.setGeneratedPackage(this.generatedPackage);
    }

    /**
     * Tear down. Delete generated files.
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        this.deleteFiles(this.generatedDirectory);
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#generateDTOs(java.util.List)}
     * .
     */
    @Test
    public void testGenerateDTOs() {
        try {
            this.dtoGeneratorImpl.classesToGenerate.add(Bean.class);
            final List<Class<?>> classesToGenerate = new ArrayList<Class<?>>();
            classesToGenerate.add(Bean.class);
            classesToGenerate.add(Bean2.class);
            this.dtoGeneratorImpl.generateDTOs(classesToGenerate);
            final File file = new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto\\Bean2DTO.java");
            Assert.assertTrue("package has not been created", new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto").exists());
            final BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;
            boolean validPackage = false;
            boolean validClass = false;
            boolean validField = false;
            boolean validFieldGetter = false;
            boolean validFieldSetter = false;
            while ((line = bf.readLine()) != null) {
                if ("package fr.maven.dto.bean.dto;".equals(line)) {
                    validPackage = true;
                }
                else if ("public class Bean2DTO implements Serializable {"
                        .equals(line)) {
                    validClass = true;
                }
                else if ("\tprivate BeanDTO bean;".equals(line)) {
                    validField = true;
                }
                else if ("\tpublic BeanDTO getBean() {".equals(line)) {
                    validFieldGetter = true;
                }
                else if ("\tpublic void setBean(BeanDTO bean) {".equals(line)) {
                    validFieldSetter = true;
                }
            }
            bf.close();
            this.deleteFiles(file);
            Assert.assertTrue("Package generated not valid", validPackage);
            Assert.assertTrue("Class generated not valid", validClass);
            Assert.assertTrue("Field generated not valid", validField);
            Assert.assertTrue("Field getter generated not valid",
                    validFieldGetter);
            Assert.assertTrue("Field setter generated not valid",
                    validFieldSetter);
        }
        catch (final IOException e) {
            Assert.fail("The class has not been generated (could not write or read file).");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#generateDTO(java.lang.Class)}
     * .
     */
    @Test
    public void testGenerateDTO() {
        try {
            this.dtoGeneratorImpl.classesToGenerate.add(Bean.class);
            this.dtoGeneratorImpl.generateDTO(Bean2.class);
            final File file = new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto\\Bean2DTO.java");
            Assert.assertTrue("package has not been created", new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto").exists());
            final BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;
            boolean validPackage = false;
            boolean validClass = false;
            boolean validField = false;
            boolean validGenericField = false;
            boolean validFieldGetter = false;
            boolean validGenericFieldGetter = false;
            boolean validFieldSetter = false;
            boolean validGenericFieldSetter = false;
            while ((line = bf.readLine()) != null) {
                if ("package fr.maven.dto.bean.dto;".equals(line)) {
                    validPackage = true;
                }
                else if ("public class Bean2DTO implements Serializable {"
                        .equals(line)) {
                    validClass = true;
                }
                else if ("\tprivate BeanDTO bean;".equals(line)) {
                    validField = true;
                }
                else if ("\tprivate java.util.List<BeanDTO> beans;"
                        .equals(line)) {
                    validGenericField = true;
                }
                else if ("\tpublic BeanDTO getBean() {".equals(line)) {
                    validFieldGetter = true;
                }
                else if ("\tpublic java.util.List<BeanDTO> getBeans() {"
                        .equals(line)) {
                    validGenericFieldGetter = true;
                }
                else if ("\tpublic void setBean(BeanDTO bean) {".equals(line)) {
                    validFieldSetter = true;
                }
                else if ("\tpublic void setBeans(java.util.List<BeanDTO> beans) {"
                        .equals(line)) {
                    validGenericFieldSetter = true;
                }
            }
            bf.close();
            this.deleteFiles(file);
            Assert.assertTrue("Package generated not valid", validPackage);
            Assert.assertTrue("Class generated not valid", validClass);
            Assert.assertTrue("Field generated not valid", validField);
            Assert.assertTrue("Generic Field generated not valid",
                    validGenericField);
            Assert.assertTrue("Field getter generated not valid",
                    validFieldGetter);
            Assert.assertTrue("Generic Field getter generated not valid",
                    validGenericFieldGetter);
            Assert.assertTrue("Field setter generated not valid",
                    validFieldSetter);
            Assert.assertTrue("Generic Field setter generated not valid",
                    validGenericFieldSetter);
        }
        catch (final IOException e) {
            Assert.fail("The class has not been generated (could not write or read file).");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#getDTOPackage(java.lang.Class)}
     * .
     */
    @Test
    public void testGetDTOPackage() {
        final Class<?> clazz = Bean.class;
        Assert.assertEquals("getPackageDTO failed", "fr.maven.dto.bean.dto",
                this.dtoGeneratorImpl.getDTOPackage(clazz));
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#isClassToGenerate(java.lang.Class)}
     * .
     */
    @Test
    public void testIsClassToGenerateWithValidClass() {
        this.dtoGeneratorImpl.classesToGenerate.add(Bean.class);
        Assert.assertTrue(
                "isClassToGenerate does not find Bean class but it should.",
                this.dtoGeneratorImpl.isClassToGenerate(Bean.class));
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#isClassToGenerate(java.lang.Class)}
     * .
     */
    @Test
    public void testIsClassToGenerateWitnValidClass() {
        Assert.assertFalse(
                "isClassToGenerate found Bean class but it should not.",
                this.dtoGeneratorImpl.isClassToGenerate(Bean.class));
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#getDTOFieldType(java.lang.Class, java.lang.reflect.Field)}
     * .
     */
    @Test
    public void testGetDTOFieldType() {
        try {
            final Field field = Bean.class.getDeclaredField("attribut1");
            Assert.assertEquals(
                    "getFieldType does not result String for attribut1 field",
                    "String",
                    this.dtoGeneratorImpl.getDTOFieldType(Bean.class, field));
        }
        catch (final SecurityException e) {
            Assert.fail("Field attribut1 not accessible.");
        }
        catch (final NoSuchFieldException e) {
            Assert.fail("Field attribut1 does not exists.");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#getDTOType(java.lang.Class, java.lang.reflect.Tyoe)}
     * .
     */
    @Test
    public void testGetDTOTypeForClassInJavaLang() {
        try {
            final Field field = Bean.class.getDeclaredField("attribut1");
            Assert.assertEquals(
                    "getFieldType does not result String for attribut1 field",
                    "String",
                    this.dtoGeneratorImpl.getDTOFieldType(Bean.class, field));
        }
        catch (final SecurityException e) {
            Assert.fail("Field attribut1 not accessible.");
        }
        catch (final NoSuchFieldException e) {
            Assert.fail("Field attribut1 does not exists.");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#getDTOType(java.lang.Class, java.lang.reflect.Tyoe)}
     * .
     */
    @Test
    public void testGetDTOTypeForParameterizedClass() {
        try {
            final Field field = Bean2.class.getDeclaredField("beans");
            Assert.assertEquals(
                    "getFieldType does not return java.util.List<fr.maven.dto.bean.Bean> for beans field",
                    "java.util.List<fr.maven.dto.bean.Bean>",
                    this.dtoGeneratorImpl.getDTOType(Bean.class,
                            field.getGenericType()));
        }
        catch (final SecurityException e) {
            Assert.fail("Field attribut1 not accessible.");
        }
        catch (final NoSuchFieldException e) {
            Assert.fail("Field attribut1 does not exists.");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#getDTOType(java.lang.Class, java.lang.reflect.Tyoe)}
     * .
     */
    @Test
    public void testGetDTOTypeForArray() {
        try {
            final Field field = Bean2.class.getDeclaredField("beanArray");
            final String dtoType = this.dtoGeneratorImpl.getDTOType(Bean.class,
                    field.getGenericType());
            Assert.assertEquals(
                    "getFieldType does not return fr.maven.dto.bean.Bean[] for beanArray field",
                    "fr.maven.dto.bean.Bean[]", dtoType);
        }
        catch (final SecurityException e) {
            Assert.fail("Field attribut1 not accessible.");
        }
        catch (final NoSuchFieldException e) {
            Assert.fail("Field attribut1 does not exists.");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#getDTOFieldPackage(java.lang.Class, java.lang.reflect.Field)}
     * .
     */
    @Test
    public void testGetDTOFieldPackage() {
        try {
            final Field field = Bean.class.getDeclaredField("attribut1");
            Assert.assertEquals(
                    "getFieldPackage does not result \"\" for attribut1 field",
                    "",
                    this.dtoGeneratorImpl.getDTOFieldPackage(Bean.class,
                            field.getGenericType()));
        }
        catch (final SecurityException e) {
            Assert.fail("Field attribut1 not accessible.");
        }
        catch (final NoSuchFieldException e) {
            Assert.fail("Field attribut1 does not exists.");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#getDTOClassFileWriter(java.lang.Class)}
     * .
     */
    @Test
    public void testGetDTOClassFileWriter() {
        try {
            final FileWriter beanFileWriter = new FileWriter(new File(""));
            final FileWriter bean2FileWriter = new FileWriter(new File(""));
            this.dtoGeneratorImpl.fileWriters.put(Bean.class, beanFileWriter);
            this.dtoGeneratorImpl.fileWriters.put(Bean2.class, bean2FileWriter);
            Assert.assertEquals(beanFileWriter,
                    this.dtoGeneratorImpl.getDTOClassFileWriter(Bean.class));
        }
        catch (final IOException e) {
            // Should not happened as the file is not created and we do not
            // write in.
        }

    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#makeDTOPackage(java.lang.Class)}
     * .
     */
    @Test
    public void testMakeDTOPackage() {
        try {
            this.dtoGeneratorImpl.makeDTOPackage(Bean.class);
            this.dtoGeneratorImpl.makeDTOClass(Bean.class);
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean.class).close();
            final File file = new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto\\BeanDTO.java");
            Assert.assertTrue("package has not been created", new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto").exists());
            final BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;
            boolean validPackage = false;
            while ((line = bf.readLine()) != null) {
                if ("package fr.maven.dto.bean.dto;".equals(line)) {
                    validPackage = true;
                }
            }
            bf.close();
            this.deleteFiles(file);
            Assert.assertTrue("Package generated not valid", validPackage);
        }
        catch (final IOException e) {
            Assert.fail("The package has not been generated (could not write or read file).");
            e.printStackTrace();
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#makeDTOClass(java.lang.Class)}
     * .
     */
    @Test
    public void testMakeDTOClass() {
        try {
            this.dtoGeneratorImpl.makeDTOPackage(Bean.class);
            this.dtoGeneratorImpl.makeDTOClass(Bean.class);
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean.class).close();
            final File file = new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto\\BeanDTO.java");
            final BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;
            boolean validClass = false;
            while ((line = bf.readLine()) != null) {
                if ("public class BeanDTO implements Serializable {"
                        .equals(line)) {
                    validClass = true;
                }
            }
            bf.close();
            this.deleteFiles(file);
            Assert.assertTrue("Class generated not valid", validClass);
        }
        catch (final IOException e) {
            Assert.fail("The class has not been generated (could not write or read file).");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#makeDTOClass(java.lang.Class)}
     * .
     */
    @Test
    public void testMakeDTOClassWithStaticFields() {
        try {
            this.dtoGeneratorImpl.makeDTOPackage(Bean.class);
            this.dtoGeneratorImpl.makeDTOClass(Bean.class);
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean.class).close();
            final File file = new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto\\BeanDTO.java");
            final BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;
            boolean validClass = false;
            while ((line = bf.readLine()) != null) {
                if ("public class BeanDTO implements Serializable {"
                        .equals(line)) {
                    validClass = true;
                }
                if (line.contains("String B;")) {
                    Assert.fail("It should generate nothing for static fields.");
                }
                if (line.contains("getB(")) {
                    Assert.fail("It should generate nothing for static fields.");
                }
                if (line.contains("setB(")) {
                    Assert.fail("It should generate nothing for static fields.");
                }
            }
            bf.close();
            this.deleteFiles(file);
            Assert.assertTrue("Class generated not valid", validClass);
        }
        catch (final IOException e) {
            Assert.fail("The class has not been generated (could not write or read file).");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#makeDTOField(java.lang.Class, java.lang.reflect.Field)}
     * .
     */
    @Test
    public void testMakeDTOField() {
        try {
            this.dtoGeneratorImpl.classesToGenerate.add(Bean.class);
            this.dtoGeneratorImpl.classesToGenerate.add(AnotherBean.class);
            this.dtoGeneratorImpl.makeDTOPackage(Bean2.class);
            this.dtoGeneratorImpl.makeDTOField(Bean2.class,
                    Bean2.class.getDeclaredField("bean"));
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean.class).close();
            final File file = new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto\\Bean2DTO.java");
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean2.class).close();
            final BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;
            boolean validField = false;
            while ((line = bf.readLine()) != null) {
                if ("\tprivate BeanDTO bean;".equals(line)) {
                    validField = true;
                }
            }
            bf.close();
            this.deleteFiles(file);
            Assert.assertTrue("Field generated not valid", validField);
        }
        catch (final IOException e) {
            Assert.fail("The field has not been generated (could not write or read file).");
        }
        catch (final SecurityException e) {
            Assert.fail("The field has not been generated (the source field is not accessible).");
        }
        catch (final NoSuchFieldException e) {
            Assert.fail("The field has not been generated (the source field is not accessible).");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#makeDTOField(java.lang.Class, java.lang.reflect.Field)}
     * .
     */
    @Test
    public void testMakeDTOFieldWithGenericMap() {
        try {
            this.dtoGeneratorImpl.classesToGenerate.add(Bean.class);
            this.dtoGeneratorImpl.classesToGenerate.add(AnotherBean.class);
            this.dtoGeneratorImpl.makeDTOPackage(Bean2.class);
            this.dtoGeneratorImpl.makeDTOField(Bean2.class,
                    Bean2.class.getDeclaredField("beansMap"));
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean.class).close();
            final File file = new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto\\Bean2DTO.java");
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean2.class).close();
            final BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;
            boolean validMapField = false;
            while ((line = bf.readLine()) != null) {
                if ("\tprivate java.util.Map<BeanDTO, AnotherBeanDTO> beansMap;"
                        .equals(line)) {
                    validMapField = true;
                }
            }
            bf.close();
            this.deleteFiles(file);
            Assert.assertTrue("MapField generated not valid", validMapField);
        }
        catch (final IOException e) {
            Assert.fail("The field has not been generated (could not write or read file).");
        }
        catch (final SecurityException e) {
            Assert.fail("The field has not been generated (the source field is not accessible).");
        }
        catch (final NoSuchFieldException e) {
            Assert.fail("The field has not been generated (the source field is not accessible).");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#makeDTOFieldGetter(java.lang.Class, java.lang.reflect.Field)}
     * .
     */
    @Test
    public void testMakeDTOFieldGetter() {
        try {
            this.dtoGeneratorImpl.classesToGenerate.add(Bean.class);
            this.dtoGeneratorImpl.makeDTOPackage(Bean2.class);
            this.dtoGeneratorImpl.makeDTOFieldGetter(Bean2.class,
                    Bean2.class.getDeclaredField("bean"));
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean.class).close();
            final File file = new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto\\Bean2DTO.java");
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean2.class).close();
            final BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;
            boolean validFieldGetter = false;
            while ((line = bf.readLine()) != null) {
                if ("\tpublic BeanDTO getBean() {".equals(line)) {
                    validFieldGetter = true;
                }
            }
            bf.close();
            this.deleteFiles(file);
            Assert.assertTrue("Field getter generated not valid",
                    validFieldGetter);
        }
        catch (final IOException e) {
            Assert.fail("The field getter has not been generated (could not write or read file).");
        }
        catch (final SecurityException e) {
            Assert.fail("The field getter has not been generated (the source field is not accessible).");
        }
        catch (final NoSuchFieldException e) {
            Assert.fail("The field getter has not been generated (the source field is not accessible).");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#makeDTOFieldGetter(java.lang.Class, java.lang.reflect.Field)}
     * .
     */
    @Test
    public void testMakeDTOFieldGetterForFieldWithOnlyOneCharacter() {
        try {
            this.dtoGeneratorImpl.makeDTOPackage(Bean.class);
            this.dtoGeneratorImpl.makeDTOFieldGetter(Bean.class,
                    Bean.class.getDeclaredField("a"));
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean.class).close();
            final File file = new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto\\BeanDTO.java");
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean2.class).close();
            final BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;
            boolean validFieldGetter = false;
            while ((line = bf.readLine()) != null) {
                if ("\tpublic String getA() {".equals(line)) {
                    validFieldGetter = true;
                }
            }
            bf.close();
            this.deleteFiles(file);
            Assert.assertTrue("Field getter generated not valid",
                    validFieldGetter);
        }
        catch (final IOException e) {
            Assert.fail("The field getter has not been generated (could not write or read file).");
        }
        catch (final SecurityException e) {
            Assert.fail("The field getter has not been generated (the source field is not accessible).");
        }
        catch (final NoSuchFieldException e) {
            Assert.fail("The field getter has not been generated (the source field is not accessible).");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#makeDTOFieldSetter(java.lang.Class, java.lang.reflect.Field)}
     * .
     */
    @Test
    public void testMakeDTOFieldSetter() {
        try {
            this.dtoGeneratorImpl.classesToGenerate.add(Bean.class);
            this.dtoGeneratorImpl.makeDTOPackage(Bean2.class);
            this.dtoGeneratorImpl.makeDTOFieldSetter(Bean2.class,
                    Bean2.class.getDeclaredField("bean"));
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean.class).close();
            final File file = new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto\\Bean2DTO.java");
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean2.class).close();
            final BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;
            boolean validFieldSetter = false;
            while ((line = bf.readLine()) != null) {
                if ("\tpublic void setBean(BeanDTO bean) {".equals(line)) {
                    validFieldSetter = true;
                }
            }
            bf.close();
            this.deleteFiles(file);
            Assert.assertTrue("Field setter generated not valid",
                    validFieldSetter);
        }
        catch (final IOException e) {
            Assert.fail("The field getter has not been generated (could not write or read file).");
        }
        catch (final SecurityException e) {
            Assert.fail("The field getter has not been generated (the source field is not accessible).");
        }
        catch (final NoSuchFieldException e) {
            Assert.fail("The field getter has not been generated (the source field is not accessible).");
        }
    }

    /**
     * Test method for
     * {@link fr.maven.dto.generator.impl.DTOGeneratorImpl#makeDTOFieldSetter(java.lang.Class, java.lang.reflect.Field)}
     * .
     */
    @Test
    public void testMakeDTOFieldSetterForFieldWithOnlyOneCharacter() {
        try {
            this.dtoGeneratorImpl.makeDTOPackage(Bean.class);
            this.dtoGeneratorImpl.makeDTOFieldSetter(Bean.class,
                    Bean.class.getDeclaredField("a"));
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean.class).close();
            final File file = new File(
                    this.generatedDirectory.getAbsolutePath()
                            + "\\fr\\maven\\dto\\bean\\dto\\BeanDTO.java");
            this.dtoGeneratorImpl.getDTOClassFileWriter(Bean2.class).close();
            final BufferedReader bf = new BufferedReader(new FileReader(file));
            String line;
            boolean validFieldSetter = false;
            while ((line = bf.readLine()) != null) {
                if ("\tpublic void setA(String a) {".equals(line)) {
                    validFieldSetter = true;
                }
            }
            bf.close();
            this.deleteFiles(file);
            Assert.assertTrue("Field setter generated not valid",
                    validFieldSetter);
        }
        catch (final IOException e) {
            Assert.fail("The field getter has not been generated (could not write or read file).");
        }
        catch (final SecurityException e) {
            Assert.fail("The field getter has not been generated (the source field is not accessible).");
        }
        catch (final NoSuchFieldException e) {
            Assert.fail("The field getter has not been generated (the source field is not accessible).");
        }
    }

    private void deleteFiles(final File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                for (final File fileChild : file.listFiles()) {
                    if (fileChild != null && fileChild.exists()) {
                        this.deleteFiles(fileChild);
                    }
                }
            }
            file.delete();
        }
    }
}
