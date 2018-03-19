package lib.alisms

import scala.concurrent.{ExecutionContext, Future}

import com.taobao.api.DefaultTaobaoClient
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest


class AliSms(config: AliSmsConfig)(implicit ec: ExecutionContext) {

  private val client = new DefaultTaobaoClient(config.client.wsUrl, config.client.appKey, config.client.secret)

  def send(phoneNumber: String, templateName: String, args: Any*) = {
    if (config.disabled) {
      Future.successful(())
    } else {
      val template = config.templates(templateName)
      val request = new AlibabaAliqinFcSmsNumSendRequest()
      // request.setExtend("")
      request.setSmsType("normal")
      request.setSmsFreeSignName(template.signName)
      request.setSmsParamString(template.paramFmtStr.format(args: _*))
      request.setRecNum(phoneNumber)
      request.setSmsTemplateCode(template.id)

      Future(client.execute(request)).flatMap { response =>
        if (response.isSuccess())
          Future.successful(())
        else
          Future.failed(new Exception(response.getSubMsg))
      }
    }
  }
}
