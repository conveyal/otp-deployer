package controllers.helpers

import views.html.helper.FieldConstructor

object BootstrapHelper {
  implicit val myFields = FieldConstructor(views.html.helper.bootstrapInput.f)
}
