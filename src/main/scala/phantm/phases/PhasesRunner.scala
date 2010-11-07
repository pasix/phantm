package phantm.phases

import phantm.Settings
import phantm.util._
import scala.util.control.Breaks._

class PhasesRunner(val reporter: Reporter) {
    def getPhasesToRun: PhaseSeq = (
        DumpsCollectionPhase
        followedBy ParsingPhase
        followedBy ASTPruningPhase
        followedBy IncludesConstantsResolutionPhase
        followedBy ASTChecksPhase
        followedBy APIImportationPhase
        followedBy SymbolsCollectionPhase
        followedBy SymbolsChecksPhase
        followedBy PureStatementsPhase
        followedBy CallGraphPhase
        followedBy MethodGraphPhase
        followedBy CFGGenerationPhase
        followedBy TypeAnalyzingPhase
        followedBy APIExportingPhase
    )

    def run(initCtx: PhasesContext) = {
        var ctx = initCtx

        val phases = getPhasesToRun.list

        breakable {
            for((ph, i) <- phases.zipWithIndex) {
                try {
                    if (Settings.get.displayProgress) {
                        println((i+1)+": "+ph.name+"...")
                    }
                    ctx = ph.run(ctx)
                } catch {
                    case e: PhaseException =>
                        reporter.error("Processing failed at phase "+(i+1)+" ("+e.ph.name+"): "+e.error)
                        break;
                }
            }
        }

        val ec  = reporter.errorsCount
        val tec = reporter.totalErrorsCount
        val nc  = reporter.noticesCount
        val tnc = reporter.totalNoticesCount

        if (!Settings.get.summaryOnly) {
            reporter.emitAll
        }

        if (Settings.get.focusOnMainFiles) {
            println(nc+" notice"+(if (nc>1) "s" else "")+" and "+ec+" error"+(if (ec>1) "s" else "")+" occured in main files.")
            println(tnc+" notice"+(if (tnc>1) "s" else "")+" and "+tec+" error"+(if (tec>1) "s" else "")+" occured in total.")
        } else {
            println(nc+" notice"+(if (nc>1) "s" else "")+" and "+ec+" error"+(if (ec>1) "s" else "")+" occured.")

        }
    }
}