package lib.play

import scala.concurrent.{ExecutionContext, Future}

import com.taobao.api.DefaultTaobaoClient
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest
import javax.inject._
import play.api.Configuration


@Singleton
class ShortMessageService @Inject() (conf: Configuration) (implicit ec: ExecutionContext) {

  private val smsConfig = conf.get[Configuration]("sms")

  private val client = {
    val clientConfig = smsConfig.get[Configuration]("aliqin.client")

    val url = clientConfig.get[String]("url")
    val appkey = clientConfig.get[String]("appkey")
    val secret = clientConfig.get[String]("secret")

    new DefaultTaobaoClient(url, appkey, secret);
  }

  private type Requestor = (String, Seq[Any]) => Future[Unit]

  private val requestors: Map[String, Requestor] = {
    val templatesConfig = conf.get[Configuration]("sms.aliqin.templates")
    templatesConfig.subKeys.map(k => k -> createRequestor(k)).toMap
  }

  private val smsDisabled = smsConfig.get[Boolean]("disabled")

  def send(to: String, templateKey: String, args: Any*) = {
    if (smsDisabled) {
      Future.successful(())
    } else {
      requestors(templateKey)(to, args)
    }
  }

  private def createRequestor(templateKey: String): Requestor = {
    val templateConfig = conf.get[Configuration](s"sms.aliqin.templates.$templateKey")

    val templateId = templateConfig.get[String]("id")
    val signName = templateConfig.get[String]("signName")
    val param = templateConfig.get[String]("param")

    (mobilePhoneNumber, args) => {
      val request = new AlibabaAliqinFcSmsNumSendRequest();
      // request.setExtend("");
      request.setSmsType("normal");
      request.setSmsFreeSignName(signName);
      request.setSmsParamString(param.format(args: _*));
      request.setRecNum(mobilePhoneNumber);
      request.setSmsTemplateCode(templateId);

      Future(client.execute(request)).
        flatMap { response =>
          if (response.isSuccess())
            Future.successful(())
          else
            Future.failed(new Exception(response.getSubMsg))
        }
    }
  }

}
