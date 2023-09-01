import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

fun main(args: Array<String>) {

  val docRoot="/google/src/cloud/dmail/tag-stripper/google3/third_party/devsite/android/en/codelabs/"
  val fileList=getCodeLabList(docRoot)
  println()
  removeTags(fileList)
}
//////////////////////////////////////////////////////////////////
fun getCodeLabList(docRoot:String):MutableList<String> {
  println("Getting codelab list.")
  val fileListFile =
    File(docRoot.plus("/codelab_list.txt"))
  val tagListFile=File(docRoot.plus("codelab_tags.txt"))
  val fileTree = File(docRoot).walkTopDown()

  // Create a string List to store all names of .md files
  val fileList = mutableListOf<String>()

  // Iterate through the filetree under .../android/en/codelabs, and store all filenames
  // with ".md" in a String array.

  for (file in fileTree) {
    if (!file.name.endsWith(".md")) continue
    if (file.name.startsWith("_")) continue
    fileList.add(file.canonicalPath)
  }

  // And then write them to a list stored in the file named in fileListFile (declared up near
  // the top of this function.
  val writer = FileWriter(fileListFile)
  for (file in fileList)
    writer.write(file + "\n")
  writer.close()
  return fileList
}
//////////////////////////////////////////////////////////////////
fun removeTags(fileList: MutableList<String>) {
  println("Removing deprecated attributes...")
  val trigger1 = "tags:"
  val trigger2 = "categories:"
  for (fileName in fileList) {
    var counter = 0
    val lines = mutableListOf<String>()
    val inputStream = FileInputStream(fileName)
    val reader = BufferedReader(InputStreamReader(inputStream))
    var addKeyword=0

    // read in the contents of the file, line-by-line...
    while (reader.ready()) {
      val line = reader.readLine()
      // ... minus the deprecated tags
      if (line.startsWith(trigger1) || line.startsWith(trigger2))
        continue
      else lines.add(line)
    }
    println()

    // spit the contents, stripped of the tags, back out into a file with the same name.
    val outputStream = FileOutputStream(fileName)
    val writer = BufferedWriter(OutputStreamWriter(outputStream))

    for (line in lines) {
      var reachedIdLine=false
      counter++
      writer.write(line)
      if (line.startsWith("id:"))
        reachedIdLine=true
      if (reachedIdLine) {
        writer.write("\nkeyword:")
        reachedIdLine=false
      }
      if (counter < lines.size)
        writer.newLine()
    }
    writer.close()
  }
}