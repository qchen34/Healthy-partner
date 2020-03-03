// Generated code from Butter Knife. Do not modify!
package com.exp.sign;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.wang.avi.AVLoadingIndicatorView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class RegisterActivity_ViewBinding implements Unbinder {
  private RegisterActivity target;

  @UiThread
  public RegisterActivity_ViewBinding(RegisterActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public RegisterActivity_ViewBinding(RegisterActivity target, View source) {
    this.target = target;

    target.fab = Utils.findRequiredViewAsType(source, R.id.fab, "field 'fab'", FloatingActionButton.class);
    target.cvAdd = Utils.findRequiredViewAsType(source, R.id.cv_add, "field 'cvAdd'", CardView.class);
    target.editText = Utils.findRequiredViewAsType(source, R.id.et_username, "field 'editText'", EditText.class);
    target.etPassword = Utils.findRequiredViewAsType(source, R.id.et_password, "field 'etPassword'", EditText.class);
    target.etReaptPassword = Utils.findRequiredViewAsType(source, R.id.et_repeatpassword, "field 'etReaptPassword'", EditText.class);
    target.male = Utils.findRequiredViewAsType(source, R.id.male, "field 'male'", RadioButton.class);
    target.rbpt = Utils.findRequiredViewAsType(source, R.id.putong, "field 'rbpt'", RadioButton.class);
    target.rbgl = Utils.findRequiredViewAsType(source, R.id.guanli, "field 'rbgl'", RadioButton.class);
    target.etname = Utils.findRequiredViewAsType(source, R.id.et_name, "field 'etname'", EditText.class);
    target.etphone = Utils.findRequiredViewAsType(source, R.id.et_phone, "field 'etphone'", EditText.class);
    target.etemail = Utils.findRequiredViewAsType(source, R.id.et_email, "field 'etemail'", EditText.class);
    target.etwight = Utils.findRequiredViewAsType(source, R.id.et_weight, "field 'etwight'", EditText.class);
    target.bio = Utils.findRequiredViewAsType(source, R.id.bio, "field 'bio'", EditText.class);
    target.loadingIndicatorView = Utils.findRequiredViewAsType(source, R.id.loading, "field 'loadingIndicatorView'", AVLoadingIndicatorView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    RegisterActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.fab = null;
    target.cvAdd = null;
    target.editText = null;
    target.etPassword = null;
    target.etReaptPassword = null;
    target.male = null;
    target.rbpt = null;
    target.rbgl = null;
    target.etname = null;
    target.etphone = null;
    target.etemail = null;
    target.etwight = null;
    target.bio = null;
    target.loadingIndicatorView = null;
  }
}
