top.hthy_ccservice_domain={};
hthy_ccservice_domain.keepalived=function()
{
	var result=true;
	try
	{
		var mybank1Iframe=$(".E-bank-myBank");
		if(mybank1Iframe.length==0){return false;}
		$("div.E-bank div.E-bank-myBank").click();
//		var mybank1Iframe = $("#perbank-content-frame").contents().find("#content-frame");
//		if(mybank1Iframe.length==0){return false;}
//		mybank1Iframe.attr("src", mybank1Iframe.attr("src")+"&random="+new Date().getTime());
	} catch (e) {return false;}
	
	try{hideBlockUI();}catch(e){}
	try{perbankAtomLocationFWhide();}catch(e){}
	try{hideBrowserDiv();}catch(e){}
	return result;
};
hthy_ccservice_domain.isSessionTimeout=function()
{
	var result=true;
	var loginAreas=$("#ICBC_login_frame").length;
	if(loginAreas==0)
	{
		result=false;
	}else
	{
		var topLoginAreas=$("#ICBC_login_frame_f").length;
		if(topLoginAreas>0)
		{
			top.window.setTimeout(function()
					{
						$.unblockUI();
					},200);
			result=true;
		}
	}
	try
	{
		var exit_block=$("#perbank-content-frame").contents().find("#content-frame").contents().find(".exit-block");
		if(exit_block.length>0)
		{
			top.window.setTimeout(function()
					{
						top.location='https://mybank.icbc.com.cn/icbc/newperbank/perbank3/frame/frame_index.jsp';
					},200);
			result=true;
		}
	}catch(e)
	{
	}
	try
	{
		var fanhui_btn=$("#perbank-content-frame").contents().find("#fanhui");
		if(fanhui_btn){	fanhui_btn.click();	}
	}catch(e)
	{
	}
	return result;
};
hthy_ccservice_domain.getBalance=function()
{
	var result=0;
	try
	{
		var mybank1Iframe =$("#perbank-content-frame").contents().find("#content-frame").contents().find("#mybank7");
		if(mybank1Iframe.length==0){return result;}
		var balance=mybank1Iframe.contents().find(".user-message-main-r-box .main-r-div-zc").text();
		result=balance;
	} catch (e) {return -1;}
	return result;
};
hthy_ccservice_domain.getSessionId=function()
{
	var result="";
	try
	{
		var dse_sessionId =top.dse_sessionId;
		if(dse_sessionId){return dse_sessionId;}
	} catch (e) {}
	return result;
};
hthy_ccservice_domain.getRandomId=function()
{
	var result="";
	try
	{
		var randomId=$("#ICBC_login_frame").contents().find("input[name='randomId']").val();
		if(randomId){return randomId;}
	} catch (e) {}
	return result;
};
hthy_ccservice_domain.initLoginForm=function(u)
{
	var result=false;
	var ICBC_login_frame_f=$("#ICBC_login_frame_f");
	var login_f_display=$("#ICBC_login_frame_f").parent().parent().css("display");
	if(login_f_display=="none")
	{
		ICBC_login_frame_f=$("#ICBC_login_frame");
	}
	if(!ICBC_login_frame_f)
	{
		return result;
	}
	try
	{
		var logonCardNum=ICBC_login_frame_f.contents().find("#logonCardNum");
		logonCardNum.focus().click().val(u);
		
		var passwordArea=ICBC_login_frame_f.contents().find("#safeEdit1");
		passwordArea.get(0).clear();
		var verifyCodeArea=ICBC_login_frame_f.contents().find("#KeyPart");
		verifyCodeArea.get(0).clear();
	}catch(e){}
	return result;
};
hthy_ccservice_domain.formSubmit=function(u)
{
	var result=false;
	var ICBC_login_frame_f=$("#ICBC_login_frame_f");
	var login_f_display=$("#ICBC_login_frame_f").parent().parent().css("display");
	if(login_f_display=="none")
	{
		ICBC_login_frame_f=$("#ICBC_login_frame");
	}
	if(!ICBC_login_frame_f)
	{
		return result;
	}
	try
	{
		ICBC_login_frame_f.get(0).contentWindow.loginSubmit();
		var checkIsRightFlag=ICBC_login_frame_f.get(0).contentWindow.setTimeout(function()
		{
			var errorHtml=ICBC_login_frame_f.contents().find("#errorstext").html();
			if(errorHtml!="")
			{
				ICBC_login_frame_f.attr("src",ICBC_login_frame_f.attr("src"));
			}
		},6000);
		result=true;
	}catch(e){}
	return result;
};