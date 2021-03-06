package phantm.helpers

import phantm.util.Reporter
import phantm.Settings
import phantm.phases.PhasesContext

object Main {
    // Defaults params
    var output: Option[String] = None;
    var helper: Helper = new Tokenizer();
    var input: Option[String] = None;

    def main(args: Array[String]): Unit = {
        try {
            Settings.set(new Settings())

            handle_options(args toList)

            val ctx = PhasesContext()

            val rep = new Reporter(input.get :: Nil)
            Reporter.set(rep)

            input match {
                case Some(path) =>
                    try {
                        output match {
                            case Some(opath) =>
                                println("Generating "+opath+"...");
                                helper.generate(path, opath, ctx)
                            case None =>
                                helper.generate(path, System.out, ctx)
                        }
                    } catch {
                        case e => System.err.println(path+": "+e.getMessage); e.printStackTrace
                    }
                case None => throw new Exception()
            }
        } catch {
            case e: Exception => usage
        }
    }

    private def handle_options(opts: List[String]): Unit = {
        opts match {
            case "-o" :: path :: xs =>
                output = Some(path); handle_options(xs)
            case "-t" :: "CFG" :: xs =>
                helper = new CFGGraph(); handle_options(xs)
            case "-t" :: "AST" :: xs =>
                helper = new ASTGraph(); handle_options(xs)
            case "-t" :: "Tokens" :: xs =>
                helper = new Tokenizer(); handle_options(xs)
            case "-t" :: "ST" :: xs =>
                helper = new STGraph(); handle_options(xs)
            case "-t" :: "lint" :: xs =>
                helper = new Lint(); handle_options(xs)
            case path :: Nil =>
                input = Some(path)
            case Nil =>
            case _ => throw new Exception()
        }
    }

    private def usage = {
        println("Usage: ./helper.sh [-t <type>] [-o <outfile>] <infile>");
        println("      Type: AST: Generate dot for the Abstract Syntax Tree");
        println("            ST: Generate dot for the Syntax Tree");
        println("            Tokens: Generate lits of tokens and their content");
    }


}
