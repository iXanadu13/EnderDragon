package pers.xanadu.enderdragon.maven;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.TestOnly;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DependencyManager {
    private static final Set<String> dependency_set = new TreeSet<>();
    private static final Logger logger = Bukkit.getLogger();
    private static boolean dep_groovy = false;
    public static void onEnable(){
        dependency_set.clear();
        List<String> rules = Collections.emptyList();
//        List<String> rules = Arrays.asList(
//                "groovy","xanadu.groovy",
//                "org.apache.groovy","xanadu.org.apache.groovy"
//        );
        Dependency groovy = new Dependency("org.apache.groovy","groovy","4.0.18","https://repo1.maven.org/maven2/", Dependency.AlgorithmType.SHA_256,rules);
        boolean f = groovy.load();
        if(f) dep_groovy = true;
        else err("Failed to load library: "+groovy.getName());

        //addDependency("org.jetbrains.kotlin","kotlin-stdlib","1.7.20");
        //addDependency("com.fasterxml.jackson.core","jackson-databind","2.14.2");
        //loadAllDependency();
    }
    @TestOnly
    private static void loadAllDependency(){
        dependency_set.forEach(key->{
            String[] splits = key.split("\\$",3);
            new Dependency(splits[0],splits[1],splits[2]).load();
        });
    }
    @TestOnly
    public static void addDependency(String groupId, String artifactId, String version,String... exclusion){
        dependency_set.add(groupId+"$"+artifactId+"$"+version);
        if(exclusion!=null && exclusion.length!=0 && "*$*".equals(exclusion[0])) return;
        String pom_url = "https://repo1.maven.org/maven2/"
                + groupId.replaceAll("\\.", "/") + "/"
                + artifactId.replaceAll("\\.","/") + "/"
                + version + "/" + artifactId + "-" + version + ".pom";
        String baseDir = "libs\\"
                + groupId.replaceAll("\\.", "\\\\") + "\\"
                + artifactId.replaceAll("\\.","\\\\") + "\\"
                + version;
        File folder = new File(baseDir);
        folder.mkdirs();
        String filePath = baseDir + "\\" + artifactId + "-" + version + ".pom";
        DependencyManager.downloadFile(pom_url,filePath);
        File file = new File(filePath);
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            Element root = doc.getDocumentElement();
            Element dependenciesElement = XMLParser.getElement(root,"dependencies");
            if(dependenciesElement == null) return;
            NodeList dependencyList = dependenciesElement.getElementsByTagName("dependency");
            for (int i = 0; i < dependencyList.getLength(); i++) {
                Element dependencyElement = (Element) dependencyList.item(i);
                String nxt_groupId = XMLParser.getString(dependencyElement,"groupId");
                String nxt_artifactId = XMLParser.getString(dependencyElement,"artifactId");
                String nxt_version = XMLParser.getString(dependencyElement,"version");
                if(nxt_groupId==null || nxt_artifactId==null || nxt_version==null){
                    return;
                }
                String scope = XMLParser.getString(dependencyElement,"scope");
                String optional = XMLParser.getString(dependencyElement,"optional");
                //${xxx}如何处理？
                //获取不到版本怎么办？
                //if(scope.matches("${}"))
                if(isRequired(scope,optional)) {
                    String[] nxt_exclusions = getExclusions(dependencyElement);
                    String id = nxt_groupId + "$" + nxt_artifactId;
                    String parent_id = nxt_groupId + "$*";
                    if(exclusion!=null){
                        boolean add = true;
                        for(int j=0;j<exclusion.length;j++){
                            if(exclusion[j].equals(id) || exclusion[j].equals(parent_id)){
                                add = false;
                                break;
                            }
                        }
                        if(add) addDependency(nxt_groupId,nxt_artifactId,nxt_version,nxt_exclusions);
                    }
                    else {
                        addDependency(nxt_groupId,nxt_artifactId,nxt_version,nxt_exclusions);
                    }
                }
            }
        }catch (IOException | ParserConfigurationException | SAXException e){
            e.printStackTrace();
        }

    }
    public static void err(String str){
        logger.severe("[EnderDragon] "+str);
    }
    public static void warning(String str){
        logger.warning("[EnderDragon] "+str);
    }
    public static void print(String str){
        logger.info("[EnderDragon] "+str);
    }

    public static boolean checkHash(File file,File hash_file,String algorithm){
        if(!file.exists() || !hash_file.exists()) return false;
        String hash = readAsString(hash_file);
        String sha_256 = getHash(file,algorithm);
        return Objects.equals(sha_256, hash);
    }
    public static boolean downloadFile(String fileUrl, String savePath) {
        print("Download "+fileUrl);
        try{
            URL url = new URL(fileUrl);
            try (BufferedInputStream in = new BufferedInputStream(url.openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }
            return true;
        }catch (IOException e){
            return false;
        }
    }
    private static String[] getExclusions(Element dependencyElement){
        Element exclusionsElement = XMLParser.getElement(dependencyElement,"exclusions");
        if(exclusionsElement!=null){
            NodeList exclusionList = exclusionsElement.getElementsByTagName("exclusion");
            String[] res = new String[exclusionList.getLength()];
            for(int j=0;j<res.length;j++){
                Element element = (Element) exclusionList.item(j);
                String tmp = XMLParser.getString(element,"groupId")+"$"+XMLParser.getString(element,"artifactId");
                res[j] = tmp;
            }
            return res;
        }
        else return new String[0];
    }
    private static boolean isRequired(String scope,String optional){
        if("true".equals(optional)) return false;
        return !"test".equals(scope) && !"provided".equals(scope) && !"system".equals(scope);
    }
    private static String readAsString(File file){
        try {
            Path path = file.toPath();
            byte[] fileBytes = Files.readAllBytes(path);
            return new String(fileBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getHash(final File file,final String algorithm){
        try {
            Path path = file.toPath();
            byte[] fileBytes = Files.readAllBytes(path);
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(fileBytes);

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }catch (IOException|NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }
    public static boolean isGroovyLoaded(){
        return dep_groovy;
    }

}
