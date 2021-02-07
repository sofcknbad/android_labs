package com.example.lab1;

import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.regex.Pattern;

public class Calculator extends Fragment {
	private EditText txt;
	private String buffer = "";
	private Double memoryValue = 0d;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_calculator, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		txt = getActivity().findViewById(R.id.expression);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			txt.setShowSoftInputOnFocus(false);
		} else {
			txt.setInputType(0x00000000);
		}
	}

	@SuppressLint("NonConstantResourceId")
	public void calculator_buttons(View view) {
		switch (view.getId()) {
			case R.id.mc:
				memoryValue = 0d;
				break;
			case R.id.m_plus:
				try {
					if (buffer.length() != 0)
						memoryValue += Double.parseDouble(buffer);
				} catch (NumberFormatException ignored) { }
				break;
			case R.id.m_minus:
				try {
					if (buffer.length() != 0)
						memoryValue -= Double.parseDouble(buffer);
				} catch (NumberFormatException ignored) { }
				break;
			case R.id.mr:
				long l = Math.round(memoryValue);
				buffer = (memoryValue == l)? ""+l:memoryValue.toString();
				txt.setText(buffer);
				txt.setSelection(txt.length());
				break;
			case R.id.clear:
				buffer = "";
				txt.setText(buffer);
				txt.setSelection(txt.length());
				((TextView)getActivity().findViewById(R.id.result)).setText("");
				break;
			case R.id.clr:
				addText("<");
				break;
			case R.id.equally:
				String s = result();
				if (!s.equals("NaN"))
					buffer = s;
				txt.setText(buffer);
				txt.setSelection(txt.length());
				break;
			default:
				addText(((Button) view).getText().toString());
				break;
		}
		String s = result();
		((TextView)getActivity().findViewById(R.id.result)).setText((s.equals("NaN")?"":s));
	}

	void addText(String forInput) {
		String left = buffer.substring(0, txt.getSelectionStart());
		String right = buffer.substring(txt.getSelectionEnd());

		if (Pattern.matches(".*[scion].*", buffer.substring(txt.getSelectionStart(),txt.getSelectionEnd()))) {
			return;
		}

		if (forInput.equals("<")){
			if (Pattern.matches(".*[sinco][(]?$", left) && Pattern.matches("^[io]?[sn]?[(]?.*", right)){
				String[] l = left.split("[sc]?[io]?[ns]?[(]?$");
				String[] r = right.split("^[io]?[ns]?[(]");
				buffer = (l.length == 0)?"":l[0];
				buffer += (r.length == 0)?"":(r.length == 1)?r[0]:r[1];
				txt.setText(buffer);
				txt.setSelection((l.length == 0)?"".length():l[0].length());
				return;
			} else if (left.length() != 0) {
				buffer = left.substring(0, left.length()-1)+right;
				txt.setText(buffer);
				txt.setSelection(left.length()-1);
				return;
			} else {
				txt.setSelection(buffer.length());
				return;
			}
		} else if (Pattern.matches(".*[sinco][(]?$", left) && Pattern.matches("^[io]?[sn]?[(].*", right)) {
			return;
		} else if (forInput.equals(".")) {
			if (Pattern.matches(".*\\d*\\.\\d*$", left) || Pattern.matches("^\\d*\\.\\d*.*", right))
				return;
		} else if (Pattern.matches("[+*/]", forInput)){
			if (left.length() == 0) return;
			else if (left.charAt(left.length()-1)=='(') return;
			else if (Pattern.matches(".*[*/][-]$", left)
					&& !Pattern.matches("^[+-/*].*", right)) left = left.substring(0, left.length()-2)+forInput;
			else if (Pattern.matches(".*[+*/-]$", left)
					&& !Pattern.matches("^[+-/*].*", right)) left = left.substring(0, left.length()-1)+forInput;
			else if (Pattern.matches("^[+-/*].*", right)) return;
			else left += forInput;
			buffer = left + right;
			txt.setText(buffer);
			txt.setSelection(left.length());
			return;
		} else if (Pattern.matches("[-]", forInput)){
			if (Pattern.matches(".*[+]$", left)
					&& !Pattern.matches("^[+-/*].*", right)) left = left.substring(0, left.length()-1);
			else if (Pattern.matches(".*[-]$", left)
					&& !Pattern.matches("^[+-/*].*", right)) return;
			else if (Pattern.matches("^[+-/*].*", right)) return;
			left += forInput;
			buffer = left + right;
			txt.setText(buffer);
			txt.setSelection(left.length());
			return;
		}

		buffer = left + forInput + right;

		int i = txt.getSelectionStart();
		txt.setText(buffer);
		txt.setSelection(i+forInput.length());
	}

	String result() {
		Expression e = new Expression(buffer);
		double dbl = e.calculate();
		if (Math.round(dbl) == dbl)
			return "" + Math.round(dbl);
		return Double.toString(dbl);
	}
}