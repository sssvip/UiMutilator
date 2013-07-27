package se.vidstige.android.uimultimator;

import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.text.html.FormSubmitEvent.MethodType;

import android.R.integer;

class ReadTestParser implements StreamParser{

	private static final String INSTRUMENTATION_STATUS = "INSTRUMENTATION_STATUS: ";
	private String classname;
	private String methodname;

	public ReadTestParser(String classname, String methodname) {
		this.classname = classname;
		this.methodname = methodname;
	}

	@Override
	public void parse(BufferedReader input) throws IOException, UiMultimatorException {

		String line;
		String lastExceptionMessage = null;
		while ((line = input.readLine()) != null)
		{
			if (line.startsWith(INSTRUMENTATION_STATUS))
			{
				String rest = line.substring(INSTRUMENTATION_STATUS.length());
				if (rest.startsWith("class="))
				{
					String classname2 = rest.substring("class=".length());
					if (!classname2.equals(classname)) {
						throw new UiMultimatorException("Expected class " + classname + " to be run, but " + classname2 + " was run");
					}
				}
				int idx = rest.indexOf("UiObjectNotFoundException: ");
				if (idx >= 0)
				{
					lastExceptionMessage = rest.substring(idx + "UiObjectNotFoundException: ".length()); 
				}
			}
			if ("FAILURES!!!".equals(line))
			{
				if (lastExceptionMessage == null)
					throw new UiMultimatorException("Could not execute " + classname + "#" + methodname + " on device");
				throw new UiMultimatorException("UiObject not found: " + lastExceptionMessage);
			}
			
			//System.out.println("line: " + line);
		}
	}
}
