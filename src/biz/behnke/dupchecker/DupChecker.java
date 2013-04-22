package biz.behnke.dupchecker;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.regex.RegexModifier;
import java.io.File;
import java.io.Reader;
import java.util.Arrays;

public class DupChecker extends JFrame {

	protected JTextArea log = new JTextArea("\nREADME\n======\nAll CVS files must have a header row.\n"
			+ "The output file will contain all entries from file 2 that do NOT exist in file 1.\n\n");

	protected JTextField file_input_old = new JTextField("/path/to/input/olddata");
	protected JTextField file_input_new = new JTextField("/path/to/input/newdata");
	protected JTextField file_output = new JTextField("/path/to/output/newfile");

	protected JComboBox
			encoding_old, separator_old, quote_old,
			encoding_new, separator_new, quote_new,
			encoding_out, separator_out, quote_out;

	protected JList moreOutput = new JList();

	protected String lastdir = "";

	/**
	 * set up combo boxes with their default values
	 */
	protected void initComboBoxes() {
		String[] encodings = new String[]{"CP865", "UTF-8", "ISO-8859-1"};
		String[] separators = new String[]{"\t", ",", ";"};
		String[] quotes = new String[]{"\0", "\"", "'"};

		encoding_old = new JComboBox(encodings);
		separator_old = new JComboBox(separators);
		quote_old = new JComboBox(quotes);

		encoding_new = new JComboBox(encodings);
		separator_new = new JComboBox(separators);
		quote_new = new JComboBox(quotes);

		encoding_out = new JComboBox(encodings);
		separator_out = new JComboBox(separators);
		quote_out = new JComboBox(quotes);
	}

	public DupChecker() {
		super("Duplicate Finder - Naturkost Nord");
		initialize();
	}

	@Override
	protected void frameInit() {
		super.frameInit();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setExtendedState(MAXIMIZED_BOTH);
	}

	/**
	 * dirty solution but works
	 */
	protected void setMargin() {
		add(new JLabel(" "), BorderLayout.NORTH);
		add(new JLabel(" "), BorderLayout.SOUTH);
		add(new JLabel("   "), BorderLayout.EAST);
		add(new JLabel("   "), BorderLayout.WEST);
	}

	/**
	 * prints cvs row to log output
	 *
	 * @param String[] next
	 */
	protected void showExample(String[] next) {
		addlog("Example output:");
		for (int i = 0; i <next.length; i++) {
			addlog("\t"+i+": "+next[i]);
		}
		addlog("");
	}

	public String getLastdir() {
		return lastdir;
	}

	public void setLastdir(String newdir) {
		lastdir = newdir;
	}

	private void initialize() {
		setMargin();

		JPanel maincontainer = new JPanel(new BorderLayout());

		/*
		 * top panel
		 */
		JPanel top = new JPanel(new GridLayout(3, 8));

		initComboBoxes();

		// OLDDATA
		top.add(file_input_old);
		JButton btn_input_old = new JButton("Inputfile Olddata");
		btn_input_old.addActionListener(new ChooseFileActionListener(this, file_input_old));
		top.add(btn_input_old);
		top.add(new JLabel("Encoding", SwingConstants.CENTER));
		top.add(encoding_old);
		top.add(new JLabel("Separator", SwingConstants.CENTER));
		top.add(separator_old);
		top.add(new JLabel("Quotes", SwingConstants.CENTER));
		top.add(quote_old);

		// NEWDATA
		top.add(file_input_new);
		JButton btn_input_new = new JButton("Inputfile Newdata");
		btn_input_new.addActionListener(new ChooseFileActionListener(this, file_input_new));
		top.add(btn_input_new);
		top.add(new JLabel("Encoding", SwingConstants.CENTER));
		top.add(encoding_new);
		top.add(new JLabel("Separator", SwingConstants.CENTER));
		top.add(separator_new);
		top.add(new JLabel("Quotes", SwingConstants.CENTER));
		top.add(quote_new);

		// OUTPUTDATA
		top.add(file_output);
		JButton btn_output = new JButton("Outputfile");
		btn_output.addActionListener(new ChooseFileActionListener(this, file_output, true));
		top.add(btn_output);
		top.add(new JLabel("Encoding", SwingConstants.CENTER));
		top.add(encoding_out);
		top.add(new JLabel("Separator", SwingConstants.CENTER));
		top.add(separator_out);
		top.add(new JLabel("Quotes", SwingConstants.CENTER));
		top.add(quote_out);

		maincontainer.add(top, BorderLayout.NORTH);

		/*
		 * log panel
		 */
		JScrollPane log_scroll = new JScrollPane(this.log);
		maincontainer.add(log_scroll, BorderLayout.CENTER);

		/*
		 * bottom panel
		 */
		JButton start = new JButton("Start duplicate check");
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					process();
				} catch (IOException e1) {
					addlog("ERROR: "+e1.getMessage());
				}
			}

		});
		maincontainer.add(start, BorderLayout.SOUTH);

		final DefaultListModel mymodel = new DefaultListModel();
		mymodel.addElement("dupchecked=1");
		moreOutput.setModel(mymodel);
		moreOutput.setToolTipText("Double click to add more fields for output file; key=value");
		moreOutput.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					String result = JOptionPane.showInputDialog(moreOutput, "New key value pair:");
					if (result != null && !result.equalsIgnoreCase("")) {
						mymodel.addElement(result);
					}
					e.consume();
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					if (moreOutput.getSelectedIndex() != -1) {
						if (JOptionPane.showConfirmDialog(separator_new, "Delete '"+moreOutput.getSelectedValue()+"'") == JOptionPane.YES_OPTION) {
							mymodel.remove(moreOutput.getSelectedIndex());
						}
					}
					e.consume();
				}
			}

		});

		JScrollPane field_scroll = new JScrollPane(this.moreOutput);
		//maincontainer.add(field_scroll, BorderLayout.EAST);

		/*
		 * init overall panel
		 */
		add(maincontainer, BorderLayout.CENTER);


	}

	/**
	 * print a message to the log
	 *
	 * @param String msg
	 */
	protected void addlog(String msg) {
		msg += "\n";
		log.append(msg);
	}

	protected String[] buildColSelect(String[] next) {
		String[] cols = new String[next.length];
		for (int i = 0; i < cols.length; i++) {
			cols[i] = ""+i;
		}
		return cols;
	}

	public static void main(String[] args) throws IOException {
		DupChecker main = new DupChecker();
		main.setBounds(10, 10, 800, 600);
		main.setVisible(true);
	}

	protected void process() throws IOException {

		// INPUT file 1
		BufferedReader bfr = new BufferedReader(new InputStreamReader(
				new FileInputStream(file_input_old.getText()), encoding_old.getSelectedItem().toString()));

		Modifier myModifier = new RegexModifier(">", 0, "\t");
		Reader modifyingReader = new ModifyingReader(bfr, myModifier);

		String[] next;
		String[] example = null;

		HashMap<String, String[]> oldmap = new HashMap<String, String[]>(10000);
		ArrayList templist = new ArrayList(10000);

		CSVReader reader = new CSVReader(modifyingReader, separator_old.getSelectedItem().toString().charAt(0), quote_old.getSelectedItem().toString().charAt(0));
		long time1 = System.currentTimeMillis();
		addlog("Read olddata from '"+file_input_old.getText()+"' with encoding "+encoding_old.getSelectedItem().toString());

		int key_column_old = 0;
		int maxlength = 0;

		while ((next = reader.readNext()) != null) {
			next = removeUnwanted(next);
			templist.add(next);

			if (next.length > maxlength) {
				maxlength = next.length;
				example = next;
			}
		}
		reader.close();

		showExample(example);
		if (JOptionPane.showConfirmDialog(this, "Check example output. Data recognized correctly?", "Check data", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}

		Object result = JOptionPane.showInputDialog(this, "Select column for duplicate check", "Select column for duplicate check", JOptionPane.QUESTION_MESSAGE, null, buildColSelect(example), "0");
		key_column_old = new Integer(result.toString()).intValue();

		Iterator<String[]> it = templist.iterator();
		while (it.hasNext()) {
			next = it.next();
			oldmap.put(next[key_column_old], next);
		}

		long time2 = System.currentTimeMillis();
		addlog("Read olddatas (" + oldmap.size() + ") took "
				+ (time2 - time1) + " ms, " + ((time2 - time1) / 1000) + " s");

		HashMap<String, String[]> newmap = new HashMap<String, String[]>(10000);

		// INPUT file 2
		bfr = new BufferedReader(new InputStreamReader(new FileInputStream(
				file_input_new.getText()), encoding_new.getSelectedItem().toString()));

		myModifier = new RegexModifier(">", 0, "\t");
		modifyingReader = new ModifyingReader(bfr, myModifier);

		addlog("Process newdata from '"+file_input_new.getText()+"' with encoding "+encoding_new.getSelectedItem().toString());

		reader = new CSVReader(modifyingReader, separator_new.getSelectedItem().toString().charAt(0), quote_new.getSelectedItem().toString().charAt(0));
		time1 = System.currentTimeMillis();
		long records = 0;
		boolean first = true;
		int key_column_new = key_column_old;

		// used for output file
		String[] column_data_2 = null;
		while ((next = reader.readNext()) != null) {
			next = removeUnwanted(next);
			if (next.length != maxlength) {
				continue;
			}
			if (first) {
				column_data_2 = next;
//				showExample(next);
//				if (JOptionPane.showConfirmDialog(this, "Check example output. Data recognized correctly?", "Check data", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
//					return;
//				}
//
//				result = JOptionPane.showInputDialog(this, "Select column for duplicate check", "Select column for duplicate check", JOptionPane.QUESTION_MESSAGE, null, buildColSelect(next), "0");
//				key_column_new = new Integer(result.toString()).intValue();

				first = false;
			}
			if (!oldmap.containsKey(next[key_column_new])) {
				newmap.put(next[key_column_new], next);
			}
			records++;
		}
		reader.close();
		time2 = System.currentTimeMillis();
		addlog("Processed " + records + " datas. Found "
				+ newmap.size() + " non-duplicates; took " + (time2 - time1)
				+ " ms, " + ((time2 - time1) / 1000) + " s");

		// OUTPUT file
		BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file_output.getText()), encoding_out.getSelectedItem().toString()));
		CSVWriter writer = new CSVWriter(bfw, separator_out.getSelectedItem().toString().charAt(0), quote_out.getSelectedItem().toString().charAt(0));
		it = newmap.values().iterator();

		DefaultListModel mymodel = (DefaultListModel)moreOutput.getModel();
		Object[] additional = mymodel.toArray();
//		String[] moreHeader = new String[additional.length];
//		String[] moreData = new String[additional.length];
//		for(int i = 0; i<additional.length; i++) {
//			String[] temp = ((String)additional[i]).split("=");
//			if (temp.length != 2) {
//				addlog("Misconfigured extra column: "+additional[i].toString()+"; skipped");
//				moreHeader[i] = "#"+i;
//				moreData[i] = "#"+i;
//				continue;
//			}
//			moreHeader[i] = temp[0];
//			moreData[i] = temp[1];
//		}

		// headline
		ArrayList<String> l;
		l = new ArrayList<String>();

		String[] headline = new String[2];

		File infile1 = new File(file_input_new.getText());
		File infile2 = new File(file_input_old.getText());

		headline[1] = "";
		headline[1] = "Artikel aus " + infile2.getName() + " nicht in " + infile1.getName();

		l.addAll(Arrays.asList(headline));
		String[] temp = l.toArray(new String[l.size()]);
		writer.writeNext(temp);

		l.clear();
		l.addAll(Arrays.asList(column_data_2));
//		l.addAll(Arrays.asList(moreHeader));

		temp = l.toArray(new String[l.size()]);
		writer.writeNext(temp);

		// data
		addlog("Write output to '"+file_output.getText()+"' with encoding "+encoding_out.getSelectedItem().toString());
		while (it.hasNext()) {
			l.clear();
			next = (String[])it.next();

			l.addAll(Arrays.asList(next));
//			l.addAll(Arrays.asList(moreData));

			writer.writeNext(l.toArray(new String[l.size()]));
		}
		writer.close();
		addlog("Finished writing output.");

	}

	private String[] removeUnwanted(String[] row) {
		for (int i = 0; i < row.length; i++) {
			row[i] = row[i].replace("_|_|_|.", "");
		}

		return row;
	}

}

