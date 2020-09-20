package application;

import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.FileChooser;

public class SampleController implements Initializable {

	@FXML
	private Label clabopp;

	@FXML
	private Label clabme;

	@FXML
	private TextArea ctxtarea;

	@FXML
	private Label clabtyping, labrecvfile;

	@FXML
	private TextField ctxtmsg;

	@FXML
	private Button cbtnsend;

	@FXML
	private Pane cpane;

	@FXML
	private TextField ctxtname;

	@FXML
	private TextField ctxtip;

	@FXML
	private TextField ctxtport;

	@FXML
	private Button btnconnect;

	@FXML
	private PieChart pie;

	@FXML
	private TextField ctxtfile;

	@FXML
	private Button cbtnimport;

	@FXML
	private Button cbtnfile;

	@FXML
	private VBox v1;

	@FXML
	private Button btnimport,btnme;

	@FXML
	private Button btnsave;

	@FXML
	private Button btnclr;

	Socket soc;
	DataInputStream dis;
	DataOutputStream dos;

	String oppname = "";

	String msg;

	int copp = 0;
	int cme = 0;

	int i = 1;

	File f;

	File openfile;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

		System.out.println("Client load");

		clabtyping.setVisible(false);

		ctxtip.setText("localhost");
		ctxtport.setText("4444");
		ctxtname.setText("Ashutosh");

		setpie(copp, cme);
		btnconnect.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				connect();

				cpane.setVisible(false);

				Tooltip t=new Tooltip(ctxtname.getText());
				t.setStyle("-fx-font-size: 20");
				
				btnme.setTooltip(t);
				

			}
		});

		cbtnsend.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				soundsend();
				sendMsg();
				if (ctxtmsg.getText().isEmpty()) {
					sendTypingAlert("no");
				}

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						setpie(copp, cme);

					}
				});

			}
		});

		ctxtmsg.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				soundsend();
				sendMsg();

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						setpie(copp, cme);

					}
				});
			}
		});

		ctxtmsg.setOnKeyReleased(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {

				if (ctxtmsg.getText().isEmpty()) {
					sendTypingAlert("no");

				} else {
					sendTypingAlert("yes");
				}

			}
		});

		btnclr.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				clear();
			}
		});
		btnsave.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						savechat();

					}
				});

			}
		});
		btnimport.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						importchat();

					}
				});

			}
		});

		cbtnfile.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub

				sendfile();
			}
		});
		cbtnimport.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub

				importfile();

			}
		});

	}

	void showfile(String file) {
		labrecvfile.setVisible(true);
		System.out.println("in show");
		HBox h = new HBox(10);
		h.setPadding(new Insets(5));

		Label lab = new Label();
		lab.setText("" + i);

		Label lab1 = new Label();
		lab1.setText(file);
		lab1.setPrefWidth(288);

		Button btn = new Button();
		btn.setText("Open");
		btn.setStyle("-fx-background-color:#1e90ff;-fx-text-fill: white;");
		btn.setPrefHeight(20);

		System.out.println("created");

		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("clicked");
				try {

					Desktop.getDesktop().open(f);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				v1.getChildren().add(h);
				h.getChildren().add(lab);
				h.getChildren().add(lab1);
				h.getChildren().add(btn);

			}
		});

	}

	void getMsg() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					try {
						msg = dis.readUTF();
						System.out.println("Msg from server :" + msg);

						if (msg.startsWith("101:")) {

							soundrecv();
							ctxtarea.appendText("\n" + oppname + " : " + msg.replace("101:", ""));
							// System.out.println("replace"+msg.replace("101:", ""));

							copp++;
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									setpie(copp, cme);

								}
							});
						} else if (msg.startsWith("102:")) {
							msg = msg.replace("102:", "");
							System.out.println("msg in 102" + msg);
							if (msg.equals("yes")) {
								clabtyping.setVisible(true);

							} else {

								clabtyping.setVisible(false);

							}

						} else if (msg.startsWith("103:")) {
							msg = msg.replace("103:", "");

							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub

									clabopp.setText(msg);

								}

							});

							oppname = msg;
						}

						else if (msg.startsWith("104:")) {
							msg = msg.replace("104:", "");

							f = new File(msg);
							System.out.println("file created");
							byte[] bytes = new byte[16 * 1024];
							dis.read(bytes);
							FileOutputStream fos = new FileOutputStream(f);
							fos.write(bytes);
							fos.close();

							showfile(msg);
							i++;
							System.out.println(i);

							/*
							 * byte[] bytes = new byte[16 * 1024]; FileOutputStream fos = new
							 * FileOutputStream(f);
							 * 
							 * int count=-1; while ((count = dis.read(bytes)) > 0) { fos.write(bytes, 0,
							 * count); }
							 */

							System.out.println("write success");

							ctxtarea.appendText("\nFile Received - " + f.getName());

						} else if (msg.startsWith("105:")) {
							msg = msg.replace("105:", "");

							// byte[] data = msg.getBytes();

							// FileOutputStream fos = new FileOutputStream(f);
							// fos.write(data);
							System.out.println("write success");

							ctxtarea.appendText("File Received - " + f.getName());

						}

						// ctxtarea.appendText("\nServer : "+msg);

					} catch (Exception e) {

					}

				}

			}
		}).start();

	}

	void clear() {
		ctxtarea.setText("");
		setpie(0, 0);
	}

	void importchat() {
		try {
			FileChooser fc = new FileChooser();

			FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("TEXT Files (*.txt)", "*.txt");
			fc.getExtensionFilters().add(txtFilter);

			openfile = fc.showOpenDialog(Main.mainstage);

			FileReader fr = new FileReader(openfile);
			String data = "";
			int n;
			while ((n = fr.read()) != -1) {
				data = data + (char) n;
			}

			fr.close();

			ctxtarea.setText(data);
			// lblnm.setText(openf.getName());

		} catch (Exception e) {
			// lblnm.setText("Retry");
			e.printStackTrace();
		}
	}

	void savechat() {
		try {
			if (openfile != null) {

				FileWriter fw = new FileWriter(openfile);
				fw.write(ctxtarea.getText());
				// lblnm.setText(openf.getName());
				fw.close();
				// lblmsg.setText("File Saved!");

			} else {
				try {
					FileChooser fc = new FileChooser();

					// FileChooser fc = new FileChooser();

					FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("TEXT Files (*.txt)",
							"*.txt");
					fc.getExtensionFilters().add(txtFilter);

					openfile = fc.showSaveDialog(Main.mainstage);

					FileWriter fw = new FileWriter(openfile);
					fw.write(ctxtarea.getText());

					fw.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			// lblnm.setText("Retry" + e.getMessage());
			e.printStackTrace();
		}

	}

	void importfile() {
		FileChooser f = new FileChooser();
		cbtnimport.setTooltip(new Tooltip("Import File"));

		openfile = f.showOpenDialog(Main.mainstage);

		ctxtfile.setText(openfile.getName());

	}

	void sendfile() {
		// Path path = Paths.get(openfile);
		try {
			byte[] data = Files.readAllBytes(openfile.toPath());
			// String str1 = new String(data);
			dos.writeUTF("104:" + openfile.getName());
			System.out.println("name send");
			dos.write(data);

			System.out.println("file send");
			ctxtarea.appendText("\nFile send - " + openfile.getName());
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void setpie(int copp, int cme) {

		// System.out.println("in pie");
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data("me", cme),
				new PieChart.Data(oppname, copp));
		pie.setData(pieChartData);

	}

	void soundsend() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					AudioClip soundClip = new AudioClip(
							Paths.get("D:\\javafx\\chatHW_Server\\for-sure.mp3").toUri().toString());
					soundClip.play();
				} catch (Exception e) {

				}

			}
		}).start();
	}

	void soundrecv() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					AudioClip soundClip = new AudioClip(
							Paths.get("D:\\javafx\\chatHW_Server\\pristine.mp3").toUri().toString());
					soundClip.play();
				} catch (Exception e) {

				}

			}
		}).start();
	}

	void sendname() {

		try {
			dos.writeUTF("103:" + ctxtname.getText());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(" Name Not send");
		}

	}

	void sendTypingAlert(String msg) {

		try {
			dos.writeUTF("102:" + msg);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Not send");
		}

	}

	void connect() {
		try {
			soc = new Socket(ctxtip.getText(), Integer.parseInt(ctxtport.getText()));
			dis = new DataInputStream(soc.getInputStream());
			dos = new DataOutputStream(soc.getOutputStream());

			getMsg();

			sendname();

			System.out.println("coonected");

		} catch (Exception e) {

		}

	}

	void sendMsg() {
		String msg = ctxtmsg.getText();
		if (!msg.isEmpty()) {
			try {
				dos.writeUTF("101:" + msg);
				ctxtarea.appendText("\nMe : " + msg);

				cme++;

				ctxtmsg.setText("");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Not send");
			}
		}

	}

}
