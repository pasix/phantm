package phantm.annotations

import phantm.symbols._
import phantm.types._

object AnnotationsStore {
    var functions = Map[String, (List[TFunction], List[Type])]();

    def collectFunctionRet(fs: FunctionSymbol, t: Type) = {
        val newData = functions.get(fs.name) match {
            case Some(data) =>
                (data._1, t :: data._2)
            case None =>
                (Nil, t :: Nil)
        }

        functions += (fs.name -> newData)

    }
    def collectFunction(fs: FunctionSymbol, ft: TFunction) = {
        val newData = functions.get(fs.name) match {
            case Some(data) =>
                (ft :: data._1, data._2)
            case None =>
                (ft :: Nil, Nil)
        }

        functions += (fs.name -> newData)
    }
}
