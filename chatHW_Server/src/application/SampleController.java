package application;

import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
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
	private Label slabopp;

	@FXML
	private Label slabme;

	@FXML
	private TextArea stxtarea;

	@FXML
	private Label slabtyping, labrecvfile;

	@FXML
	private TextField stxtmsg;

	@FXML
	private Button sbtnsend, sbtnfile;

	@FXML
	private Pane spane, spaneload;

	@FXML
	private TextField stxtname;

	@FXML
	private TextField stxtport;

	@FXML
	private Button btnstart;

	@FXML
	private PieChart pie;

	@FXML
	private TextField stxtfile;

	@FXML
	private Button sbtnimport;

	@FXML
	private VBox v1;
	
	@FXML
	private Button btnimport;

	@FXML
	private Button btnsave,btnme;

	@FXML
	private Button btnclr;

	ServerSocket ser;
	Socket soc;
	DataInputStream dis;
	DataOutputStream dos;

	String oppname = "";

	String msg;

	int sopp = 0;
	int sme = 0;

	int i ;

	File openfile;

	File f;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

		System.out.println("Server load");

		slabtyping.setVisible(false);

		stxtport.setText("4444");

		stxtname.setText("Push");
		
		setpie(sopp, sme);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				btnstart.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						// TODO Auto-generated method stub

						spane.setVisible(false);
						connect();

						Tooltip t=new Tooltip(stxtname.getText());
						t.setStyle("-fx-font-size: 20");
						
						btnme.setTooltip(t);
						
						showfile();
						
					
						

						// sendname();
					}
				});

			}
		});

		sbtnsend.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub

				soundsend();

				sendMsg();
				if (stxtmsg.getText().isEmpty()) {
					sendTypingAlert("no");
				}

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						setpie(sopp, sme);

					}
				});

			}
		});

		stxtmsg.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub

				soundsend();
				sendMsg();

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						setpie(sopp, sme);

					}
				});
			}
		});

		stxtmsg.setOnKeyReleased(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {

				// System.out.println("msg=" + stxtmsg.getText());
				if (stxtmsg.getText().isEmpty()) {
					sendTypingAlert("no");
					// System.out.println("no send" + stxtmsg.getText());
				} else {
					sendTypingAlert("yes");
				}

			}
		});

		sbtnfile.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub

				sendfile();
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
		
		sbtnimport.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub

				importfile();

			}
		});

	}
	
	void clear() {
		stxtarea.setText("");
		setpie(0,0);
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

			stxtarea.setText(data);
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
				fw.write(stxtarea.getText());
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
					fw.write(stxtarea.getText());

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

	

	


	void sendTypingAlert(String msg) {

		try {
			dos.writeUTF("102:" + msg);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Not send");
		}

	}

	void showfile() {
		
		System.out.println("in show");
		
		File folder = new File("Downloads");
		File[] listOfFiles = folder.listFiles();
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

	
		v1.getChildren().clear();
		
			}
		});
		
		

		for ( i = 0; i < listOfFiles.length; i++) 
			
		{
		HBox h = new HBox(10);
		h.setPadding(new Insets(5));

		Label lab = new Label();
		lab.setText((i+1)+"");

		Label lab1 = new Label();
		lab1.setText(listOfFiles[i].getName());
		lab1.setPrefWidth(288);

		Button btn = new Button();
		btn.setText("Open");
		btn.setId(""+i);

		btn.setStyle("-fx-background-color:#1e90ff;-fx-text-fill: white;");
		btn.setPrefHeight(20);
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("clicked");
				try {
					
					System.out.println("clicked"+btn.getId());
					int n=Integer.parseInt(btn.getId());
					Desktop.getDesktop().open(listOfFiles[n]);
			
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		System.out.println(listOfFiles[i]);

		System.out.println("created"+i);

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

	}
	
	
	void sendname() {

		try {
			dos.writeUTF("103:" + stxtname.getText());
			System.out.println("name sent");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(" Name Not send");
		}

	}

	void getMsg() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					try {
						msg = dis.readUTF();
						System.out.println("Msg from Clinet :" + msg);

						if (msg.startsWith("101:")) {

							soundrecv();
							stxtarea.appendText("\n" + oppname + " : " + msg.replace("101:", ""));
							sopp++;

							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									setpie(sopp,sme);

								}
							});

						} else if (msg.startsWith("102:")) {
							msg = msg.replace("102:", "");
							if (msg.equals("yes")) {
								slabtyping.setVisible(true);

							} else {
								slabtyping.setVisible(false);

							}

						} else if (msg.startsWith("103:")) {

							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub

									msg = msg.replace("103:", "");

									slabopp.setText(msg);

									oppname = msg;
									// System.out.println("op"+oppname);

								}
							});

						}

						else if (msg.startsWith("104:")) {
							msg = msg.replace("104:", "");

							f = new File("Downloads\\"+msg);
							System.out.println("file created");

							byte[] bytes = new byte[16 * 1024];
							dis.read(bytes);
							FileOutputStream fos = new FileOutputStream(f);
							fos.write(bytes);
							fos.close();
							System.out.println("write success");

							stxtarea.appendText("\nFile Received - " + f.getName());

							showfile();
							
							System.out.println(i);

						} 
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(e.getMessage());
					}

				}

			}
		}).start();

	}

	void importfile() {
		FileChooser f = new FileChooser();
		sbtnimport.setTooltip(new Tooltip("Import File"));

		openfile = f.showOpenDialog(Main.mainstage);

		stxtfile.setText(openfile.getName());

	}

	void sendfile() {
		// Path path = Paths.get(openfile);
		try {
			byte[] data = Files.readAllBytes(openfile.toPath());
			// String str1 = new String(data,StandardCharsets.UTF_8);

			dos.writeUTF("104:" + openfile.getName());
			System.out.println("name send");
			dos.write(data);

			System.out.println("file send");
			stxtarea.appendText("\nFile send - " + openfile.getName());
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void sendMsg() {
		String msg = stxtmsg.getText();
		if (!msg.isEmpty()) {
			try {
				dos.writeUTF("101:" + msg);

				stxtarea.appendText("\nMe : " + msg);

				sme++;
				System.out.println("sme" + sme);

				stxtmsg.setText("");
				// System.out.println("in senmsg" + stxtmsg.getText());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Not send");
			}
		}

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

	void setpie(int sopp,int sme) {

		// System.out.println("in pie");
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data("me", sme),
				new PieChart.Data(oppname, sopp));
		pie.setData(pieChartData);

		System.out.println("me" + sme);
		System.out.println("you" + sopp);

	}

	void connect() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					ser = new ServerSocket(Integer.parseInt(stxtport.getText()));

					spaneload.setVisible(true);
					System.out.println("searching");
					soc = ser.accept();
					btnstart.setDisable(true);
					dis = new DataInputStream(soc.getInputStream());
					dos = new DataOutputStream(soc.getOutputStream());
					sendname();
					getMsg();

					System.out.println("connected");
					spaneload.setVisible(false);
				

					System.out.println("pie");

				} catch (Exception e) {

				}

			}
		}).start();
	}

}
