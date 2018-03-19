package lib.alisms


case class AliSmsConfig(
  disabled: Boolean,
  client: ClientConfig,
  templates: Map[String, TemplateConfig]
)

case class ClientConfig(wsUrl: String, appKey: String, secret: String)

case class TemplateConfig(id: String, signName: String, paramFmtStr: String)
