package com.zhangqing.whutwifi;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Mobile2Fragment extends Fragment {

    private Button buttonSaveSetting;
    private Button buttonConvert;
    private Button buttonConvertHelp;
    private EditText editTextMac;
    private EditText editTextSwitchip;
    private EditText editTextIp;
    private EditText editTextAcid;
    private EditText editTextPostscript;
    private SharedPreferences sharedPreferences;

    private SeekBar seekbar;
    private TextView seekbarText;
    private Button buttonClearAll;
    private CheckBox checkbox;

    private int whichFragment;

    public Mobile2Fragment() {
        super();

    }

    public void setWhichFragment(int whichFragment) {
        this.whichFragment = whichFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View messageLayout = inflater.inflate(R.layout.mobile2_layout,
                container, false);

        buttonSaveSetting = (Button) messageLayout
                .findViewById(R.id.mobile2_button_savesetting);
        buttonConvert = (Button) messageLayout
                .findViewById(R.id.mobile2_button_convert);
        buttonConvertHelp = (Button) messageLayout
                .findViewById(R.id.mobile2_button_convert_help);

        editTextMac = (EditText) messageLayout
                .findViewById(R.id.mobile2_edittext_mac);
        editTextSwitchip = (EditText) messageLayout
                .findViewById(R.id.mobile2_edittext_switchip);
        editTextIp = (EditText) messageLayout
                .findViewById(R.id.mobile2_edittext_ip);
        editTextAcid = (EditText) messageLayout
                .findViewById(R.id.mobile2_edittext_acid);
        editTextPostscript = (EditText) messageLayout
                .findViewById(R.id.mobile2_edittext_postscript);

        seekbar = (SeekBar) messageLayout
                .findViewById(R.id.button_height_seekBar);
        seekbarText = (TextView) messageLayout
                .findViewById(R.id.button_height_text);
        buttonClearAll = (Button) messageLayout
                .findViewById(R.id.mobile2_button_clearall);
        checkbox = (CheckBox) messageLayout.findViewById(R.id.mobile2_checkbox);

        sharedPreferences = getActivity().getSharedPreferences("myconfig",
                Context.MODE_PRIVATE);
        editTextMac.setText(sharedPreferences.getString("mobile"
                + whichFragment + "mac", ""));
        editTextSwitchip.setText(sharedPreferences.getString("mobile"
                + whichFragment + "switchip", ""));
        editTextIp.setText(sharedPreferences.getString("mobile" + whichFragment
                + "ip", ""));
        editTextAcid.setText(sharedPreferences.getString("mobile"
                + whichFragment + "acid", ""));
        editTextPostscript.setText(sharedPreferences.getString("mobile"
                + whichFragment + "postscript", ""));

        seekbar.setProgress(sharedPreferences.getInt("mobile" + whichFragment
                + "height", 100));
        seekbarText.setText(seekbar.getProgress() + "%");

        checkbox.setChecked(isEnable(whichFragment));
        checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putInt("mobile" + whichFragment + "enable", 1);
                } else {
                    editor.putInt("mobile" + whichFragment + "enable", 0);
                }
                editor.commit();
                Toast.makeText(getActivity(), "切换成功，重启软件生效", Toast.LENGTH_LONG)
                        .show();
            }
        });

        if (!("".equals(editTextMac.getText().toString()))) {
            buttonSaveSetting.setText("修改设置");
            editTextMac.setEnabled(false);
            editTextSwitchip.setEnabled(false);
            editTextIp.setEnabled(false);
            editTextAcid.setEnabled(false);
            editTextPostscript.setEnabled(false);
            buttonConvert.setEnabled(false);
        }

        buttonClearAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new AlertDialog.Builder(getActivity())
                        .setTitle("警告")
                        .setMessage("即将删除该环境下所有配置并重置按钮高度，是否继续？")
                        .setPositiveButton("继续",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        Editor editor = sharedPreferences
                                                .edit();
                                        editor.putString("mobile"
                                                + whichFragment + "mac", "");
                                        editor.putString("mobile"
                                                + whichFragment + "ip", "");
                                        editor.putString("mobile"
                                                        + whichFragment + "switchip",
                                                "");
                                        editor.putString("mobile"
                                                + whichFragment + "acid", "");
                                        editor.putString("mobile"
                                                + whichFragment + "name", "");
                                        editor.putInt("mobile" + whichFragment
                                                + "height", 100);
                                        editor.commit();
                                        Toast.makeText(getActivity(),
                                                "清空成功，重启软件生效",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }).setNegativeButton("返回", null).show();
            }
        });

        buttonConvertHelp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new AlertDialog.Builder(getActivity()).setTitle("帮助")
                        .setMessage(R.string.convert_help)
                        .setPositiveButton("确定", null).show();
            }
        });

        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                Editor editor = sharedPreferences.edit();
                editor.putInt("mobile" + whichFragment + "height",
                        seekBar.getProgress());
                editor.commit();
                Toast.makeText(getActivity(), "保存成功，重启软件生效", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                if (fromUser == false) {
                    return;
                }
                seekbarText.setText(progress + "%");

            }
        });

        buttonSaveSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonSaveSetting.getText() == "修改设置") {
                    buttonSaveSetting.setText("保存设置");
                    editTextMac.setEnabled(true);
                    editTextSwitchip.setEnabled(true);
                    editTextIp.setEnabled(true);
                    editTextAcid.setEnabled(true);
                    editTextPostscript.setEnabled(true);
                    buttonConvert.setEnabled(true);
                    return;
                }
                Editor editor = sharedPreferences.edit();
                editor.putString("mobile" + whichFragment + "mac", editTextMac
                        .getText().toString());
                editor.putString("mobile" + whichFragment + "switchip",
                        editTextSwitchip.getText().toString());
                editor.putString("mobile" + whichFragment + "ip", editTextIp
                        .getText().toString());
                editor.putString("mobile" + whichFragment + "acid",
                        editTextAcid.getText().toString());
                editor.putString("mobile" + whichFragment + "postscript",
                        editTextPostscript.getText().toString());
                editor.commit();

                buttonSaveSetting.setText("修改设置");
                editTextMac.setEnabled(false);
                editTextSwitchip.setEnabled(false);
                editTextIp.setEnabled(false);
                editTextAcid.setEnabled(false);
                editTextPostscript.setEnabled(false);
                buttonConvert.setEnabled(false);
                Toast.makeText(getActivity(), "保存完成", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        buttonConvert.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ClipboardManager clipboardManager = (ClipboardManager) getActivity()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                // clipboardManager.setPrimaryClip(ClipData.newPlainText(null,
                // "内容"));
                String result = "";
                if (clipboardManager.hasPrimaryClip()) {
                    result = clipboardManager.getPrimaryClip().getItemAt(0)
                            .getText().toString();
                }

                if (result.indexOf("&switchip=") == -1
                        || result.indexOf("&ip=") == -1
                        || result.indexOf("&mac=") == -1) {

                    Toast.makeText(getActivity(),
                            "认证地址格式错误\r\n剪贴板内容：" + result, Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                editTextMac.setText(WhutwifiActivity.getBetween(result,
                        "&mac=", "&"));
                editTextSwitchip.setText(WhutwifiActivity.getBetween(result,
                        "&switchip=", "&"));
                editTextIp.setText(WhutwifiActivity.getBetween(result, "&ip=",
                        "&"));

                if (result.indexOf("&ac_id=") == -1) {
                    Toast.makeText(getActivity(), "acid参数请自己填写，宿舍楼填写6，教学楼填写13",
                            Toast.LENGTH_LONG).show();
                } else {
                    editTextAcid.setText(WhutwifiActivity.getBetween(result,
                            "&ac_id=", "&"));
                    Toast.makeText(getActivity(), "获取成功！记得点击[保存设置]哦~~",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
        return messageLayout;
    }

    private boolean isEnable(int i) {
        int isEnable = sharedPreferences.getInt("mobile" + i + "enable", -1);

        switch (isEnable) {
            case -1:

                String mac = sharedPreferences.getString("mobile" + i + "mac", "");
                String name = sharedPreferences
                        .getString("mobile" + i + "name", "");
                if (name.equals("") && mac.equals("")) {
                    return false;
                } else {
                    return true;
                }

            case 0:
                return false;
            case 1:
                return true;
        }
        return false;

    }
}
