package filelister

import akka.actor._
import java.io.File

class DirectoryExplorer extends Actor with ActorLogging {
	import filelister.FileLister._
	
	var fileList = Seq[File]()
	var remainingChilds = -1
	
	def receive: Receive = {
	  case ListFiles(dir) => {
	    val list = dir.listFiles.toList
	    fileList = list.filter(_.isFile)
	    val dirs = list.filterNot(fileList contains _)
	    
	    for(d <- dirs) {
	    	val explorer = context.actorOf(Props[DirectoryExplorer])
	    	explorer ! ListFiles(d)
	    }
	    remainingChilds = dirs.size
	    
	    if(dirs.size == 0) {
	    	sender ! FileList(fileList)
	    	context.stop(self)
	    }
	    else {
	    	context.become(listFiles(dir, sender))
	    }
	  }
	}
	
	def listFiles(directory: File, requester: ActorRef): Receive = {
	  case FileList(list) => {
	    fileList = fileList ++ list
	    remainingChilds = remainingChilds - 1
	    if(remainingChilds == 0) {
	      requester ! FileList(fileList)
	      context.stop(self)
	    }
	  }
	}
	
}