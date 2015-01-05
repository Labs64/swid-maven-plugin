import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

File directory = new File("target/it/generate-extension/target");
File[] files = FileUtils.listFiles(directory, new WildcardFileFilter("*.swtag"), null);

println directory.absolutePath
println files

assert files.length > 0
