package filelister

import akka.actor._
import filelister.FileLister._
import java.io.File

class Main extends Actor with ActorLogging {
    
	val lister = context.actorOf(Props[FileLister])
	val dir = readLine("Choose directory > ")
	var startTime = System.currentTimeMillis
	
	lister ! ListFiles(new File(dir))
	
	def receive: Receive = {
	  case FileList(list) => { 	
	    println(s"(${list.size} files - Done in ${System.currentTimeMillis - startTime}ms)")
	    context.stop(lister)
	    context.stop(self)
	  }
	}
	
}