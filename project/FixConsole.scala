import sbt._

// Remove some settings from scalacOptions for console: REPLs are meant to be a little bit "fast and loose"
object FixConsole extends AutoPlugin {
  import Keys._

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  override lazy val projectSettings = Seq(
    Compile / console / scalacOptions ~= filter,
    Test / console / scalacOptions ~= filter
  )

  val excludedOptions =
    Set("unused-import", "unused", "numeric-widen").map(p => s"-Ywarn-$p") ++ // warnings
    Set("fatal-warnings", "lint").map(p => s"-X")

  val filter: Seq[String] => Seq[String] = _.filterNot(excludedOptions)
}
