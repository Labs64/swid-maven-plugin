import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.WildcardFileFilter

File directory = new File("target/it/generate-custom-attr/target/swid");
File[] files = FileUtils.listFiles(directory, new WildcardFileFilter("*.xml"), null);

println directory.absolutePath
println files

assert files.length > 0
