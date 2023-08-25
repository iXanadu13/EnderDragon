package pers.xanadu.enderdragon.maven;

import me.lucko.jarrelocator.JarRelocator;
import me.lucko.jarrelocator.Relocation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static pers.xanadu.enderdragon.maven.DependencyManager.*;

public class Dependency {
    protected final String groupId;
    protected final String artifactId;
    protected final String version;
    private final String repository;
    private final AlgorithmType algorithm;
    protected final String path;
    private final List<String> relocate_rules_str;
    private final List<Relocation> rules = new ArrayList<>();
    public Dependency(String groupId, String artifactId, String version){
        this(groupId,artifactId,version,"https://repo1.maven.org/maven2/",AlgorithmType.SHA_1, Collections.emptyList());
    }
    public Dependency(String groupId,String artifactId,String version,String repository,AlgorithmType algo,List<String> relocate_rules){
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.repository = repository.endsWith("/")?repository:repository+"/";
        this.algorithm = algo;
        this.relocate_rules_str = relocate_rules;
        this.path = groupId.replaceAll("\\.","/") + "/"
                + artifactId.replaceAll("\\.","/") + "/"
                + version;
        int rule_size = relocate_rules.size()/2;
        for(int i=0;i<rule_size;i++){
            this.rules.add(new Relocation(relocate_rules.get(i*2),relocate_rules.get(i*2+1)));
        }
    }
    public boolean load(){
        String depend_name = groupId+":"+artifactId+":"+version;
        print("Loading library " + depend_name);
        String baseDir = getBaseDir();
        File folder = new File(baseDir);
        folder.mkdirs();
        //遇到需要重定向的依赖，先检验当前重定向文件hash，验证无误就直接加载
        if(requireRelocate()){
            String relocated_name = getRelocatedFileName();
            File relocated = new File(baseDir,relocated_name);
            File relocated_hash_file = new File(baseDir,getHashFileName(relocated_name));
            if(checkHash(relocated,relocated_hash_file,algorithm.value())){
                ClassLoader loader = JarLoader.addPath(relocated.toPath());
                return loader != null;
            }
        }
        String jar_name = artifactId+"-"+version+".jar";
        String hashFile_name = getHashFileName(jar_name);
        File jarFile = new File(baseDir,jar_name);
        File hashFile = new File(baseDir,hashFile_name);
        //检验原始文件hash，不一致就重新下载
        if(!checkHash(jarFile,hashFile,algorithm.value())){
            String url_head = repository + path + "/";
            String jar_url = url_head + jar_name;
            String jar_savePath = jarFile.getPath();
            String hashFile_url = url_head + hashFile_name;
            String hashFile_savePath = hashFile.getPath();
            if(downloadFile(jar_url,jar_savePath) && downloadFile(hashFile_url,hashFile_savePath)){
                jarFile = new File(baseDir,jar_name);
                hashFile = new File(baseDir,hashFile_name);
                if(!checkHash(jarFile,hashFile,algorithm.value())){
                    err("Hash check failed for downloaded file " + jar_name);
                    return false;
                }
            }
            else{
                err("Failed to download file " + jar_name);
                return false;
            }
        }
        //原始文件已无误
        ClassLoader loader;
        if(requireRelocate()){
            String relocated_name = getRelocatedFileName();
            File relocated = new File(baseDir,relocated_name);
            JarRelocator relocator = new JarRelocator(jarFile, relocated, rules);
            print("Relocating library " + depend_name);
            try {
                relocator.run();
                generateHashFile(new File(baseDir,relocated_name));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            loader = JarLoader.addPath(relocated.toPath());
        }
        else{
            loader = JarLoader.addPath(jarFile.toPath());
        }
        return loader != null;
    }
    private void generateHashFile(File file){
        String hash = DependencyManager.getHash(file,algorithm.value());
        File hashFile = new File(getBaseDir(),getHashFileName(file.getName()));
        try{
            FileWriter writer = new FileWriter(hashFile);
            if(hash!=null) writer.write(hash);
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String getHashFileName(String jarName){
        return jarName+"."+algorithm.getFileExtension();
    }
    private String getRelocatedFileName(){
        return artifactId+"-"+version+"-Rel"+hashCode()+".jar";
    }
    private String getBaseDir(){
        return "libs/"+path;
    }
    private boolean requireRelocate(){
        return !rules.isEmpty();
    }
    public String getName(){
        return groupId+":"+artifactId+":"+version;
    }
    @Override
    public int hashCode(){
        return Objects.hash(getName()+"$"+repository+"$"+algorithm.value(),relocate_rules_str);
    }
    public enum AlgorithmType{
        MD5,SHA_1,SHA_256,SHA_512;
        private String value(){
            return this.name().replace('_','-');
        }
        private String getFileExtension(){
            return this.name().replaceAll("_","").toLowerCase();
        }
    }
}
