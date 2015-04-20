import sbt._
import sbt.IO._
import org.yaml.snakeyaml._
import java.io.FileInputStream

object Yaml {

  def setVariable(file: File, name: String, value: String) = {
    val options = new DumperOptions
    options.setExplicitStart(true)
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

    val yaml = new Yaml(options)
    val map = yaml.load(read(file)).asInstanceOf[java.util.LinkedHashMap[String, String]]
    map.put(name, value)
    write(file, yaml.dump(map))
  }
}
