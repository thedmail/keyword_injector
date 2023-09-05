import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

fun main(args: Array<String>) {

  val docRoot="/google/src/cloud/dmail/codelab-faceted-search/google3/third_party/devsite/android/en/codelabs"
  val keywordMap=getKeywordMap(docRoot)
  writeKeywords(keywordMap)
  println()
}
//////////////////////////////////////////////////////////////////
fun getKeywordMap(docRoot:String):MutableMap<String,String> {
  println("Getting keyword map.")
  val dataFile =
    File(docRoot.plus("/keyword_map.csv"))

  // Create a string List to store all names of the codelab-and-keyword maps
  val fileInfo = mutableListOf<String>()
  val keywordMap= mutableMapOf<String,String>()

  // Read the list into fileInfo

  val inputStream = FileInputStream(dataFile)
  val reader = BufferedReader(InputStreamReader(inputStream))

  // read in the contents of the file, line-by-line...
  while (reader.ready()) {
    val line = reader.readLine()
    // If there are redundant delimiters (i.e., trailing commas), remove them.
    if (line.endsWith(",")) {
        val trailingCommas=line.toCharArray()
        var counter=0
        var position=line.length-1
      while (trailingCommas[position]==',') {
        counter++
        position--
      }
      val removeTrailingCommas=line.dropLast(counter)
      fileInfo.add(removeTrailingCommas)
    } else
      fileInfo.add(line)
  }

  // split each line into a file-and-keyword map
  for (file in fileInfo) {
    keywordMap.put(file.substringBefore(",ignore"),file.substringAfter("ignore,"))
  }
  println()
  return keywordMap
}
//////////////////////////////////////////////////////////////////
fun writeKeywords (keywordMap:MutableMap<String,String>) {
  // For each file in the map...
  for (file in keywordMap.keys) {
    val fileContents= mutableListOf<String>()
    // ...read in the contents of the file, line-by-line...
    val inputStream = FileInputStream(file)
    val reader = BufferedReader(InputStreamReader(inputStream))
    // (but skip a keywords line if it's already there).
    while (reader.ready()) {
      val line = reader.readLine()
      if (line.startsWith("keywords:")) continue
      fileContents.add(line)
    }
    // ...and now spit the contents back out into the same file, but
    // with a new "keywords" line. It comes right after the "id:" element
    // because there's always one of those, and it's easiest to have a consistent
    // thing to anchor to, so to speak.
      val writer=FileWriter(file)
        for (line in fileContents) {
          writer.write("$line\n")
          if (line.startsWith("id:")) {
            writer.write("keywords: ${keywordMap.get(file)}\n")
          }
      }
        writer.close()
    }

  }

/*

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
} */