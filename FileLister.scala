package filelister

import java.io.File
import akka.actor._
import scala.concurrent.duration._

object FileLister {
  trait Operation
  trait OperationReply
  case class ListFiles(directory: File) extends Operation
  case class FileList(list: Seq[File]) extends OperationReply
}

class FileLister extends Actor with ActorLogging {
  import filelister.FileLister._

  var rootExplorer = context.actorOf(Props[DirectoryExplorer], "root")

  def receive: Receive = normal

  val normal: Receive = {
    case ListFiles(directory) => {
      rootExplorer ! ListFiles(directory)
      context.become(waiting(sender))
    }
  }

  def waiting(requester: ActorRef): Receive = {
    case l @ FileList(list) if sender == rootExplorer => {
      requester ! l
      //context.stop(rootExplorer)
    }
  }
}