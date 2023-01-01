package shared

import zio.Chunk
import zio.parser.{Regex, Syntax}

// see this for a nice challenge
// https://github.com/kitlangton/zio-app/blob/87d9133118c7d868452a8d97695ca492dc2427d6/cli/src/main/scala/database/ast/SqlSyntax.scala

object HttpFormDataParser {

  final case class FormEntry(field: String, value: String)

  final case class FormData(entries: List[FormEntry])

  val acceptableInput: Regex =
    Regex.anyAlphaNumeric | Regex.charIn('-', '_', '.')

  val acceptableInputString: Syntax[String, Char, Char, String] =
    Syntax
      .regexChar(acceptableInput, "form field has unacceptable input")
      .repeat
      .transform(
        _.toList.mkString,
        (s: String) => Chunk.fromIterable(s)
      )

  val equalSignSyntax: Syntax[String, Char, Char, Unit] =
    Syntax.char('=')

  val etSignSyntax: Syntax[String, Char, Char, Unit] =
    Syntax.char('&')

  val formEntrySyntax: Syntax[String, Char, Char, FormEntry] =
    (acceptableInputString ~ equalSignSyntax ~ acceptableInputString)
      .transform(
        { case (field, value)          => FormEntry(field, value) },
        { case FormEntry(field, value) => (field, value) }
      )

  val formSyntax: Syntax[String, Char, Char, FormData] =
    formEntrySyntax
      .repeatWithSep(etSignSyntax)
      .transform(
        entries => FormData(entries.toList), { case FormData(entries) => Chunk.fromIterable(entries) }
      )
}
