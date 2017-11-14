routesGenerator := InjectedRoutesGenerator

routesImport ++= Seq(
  "be.venneborg.refined.play.RefinedPathBinders._",
  "be.venneborg.refined.play.RefinedQueryBinders._",
  "be.venneborg.model._"
)
