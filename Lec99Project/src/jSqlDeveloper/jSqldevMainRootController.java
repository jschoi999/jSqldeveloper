package jSqlDeveloper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;

public class jSqldevMainRootController implements Initializable{

	
	private Stage primaryStage;
	
	@FXML TextArea txtSqlArea;
	@FXML TableView<HashMap<String, Object>> tabView;
	@FXML ListView<String> listView;
	@FXML StackPane stackPane;
	@FXML TextArea txtResult;
	@FXML TextField txtSize;
	@FXML ComboBox<String> comboColor;
	
	Connection conn;
	Statement pstm;  
    ResultSet rs;
    File selectedFile=null;
    
    //alert flag
    final int textFont = 1;
    final int endForConnection = 2;
    final int noUser = 3;
    final int fontSize = 4;
    
    
    public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//콤보박스에 넣을 아이템 생성 및 초기값 설정
		ObservableList<String> items = FXCollections.observableArrayList(
					new String("Black"),
					new String("Green"),
					new String("Yellow"),
					new String("Blue"),
					new String("Magenta"),
					new String("Red")
				
				);
		
		comboColor.setItems(items);
		comboColor.getSelectionModel().select(0);
		
		txtSqlArea.setFont(Font.font(25));
		
		txtSize.textProperty().addListener(new ChangeListener<String>() {
			//텍스트 사이즈 변경을 위한 속성감시부분
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				
				if(newValue == null || newValue.equals("")) {
					newValue="25";
				}
				
				
				try {
					
					if(Integer.parseInt(newValue)>150) {
						newValue="150";
						txtSize.setText("150");
						handleAlert(fontSize); //폰트 크기를 150까지만 가능하다는 메시지 출력
					}
					
				txtSqlArea.setFont(new Font(Integer.parseInt(newValue)));
				
				}catch(NumberFormatException e) {
					
						try {
							handleAlert(textFont);//숫자만 입력해달라는 메시지박스 출력
							txtSize.setText("25");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
					 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	
	}
	public void handleNew(ActionEvent e) {
		txtSqlArea.clear();
		primaryStage.setTitle("jSqlDeveloper");
	}
	public void handleOpen(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		//오픈 가능한 파일 설정
		fileChooser.getExtensionFilters().add(new ExtensionFilter("SQL File", "*.sql"));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("All File", "*.*"));
		
		
		selectedFile = fileChooser.showOpenDialog(primaryStage);
		
		if(selectedFile !=null) {
			
			
			primaryStage.setTitle("jSqlDeveloper - "+selectedFile.getPath());
			
			
			Thread thread = new Thread() { //파일이 클경우 전체 프로그램이 먹통이되므로 스레드로 실시
			@Override
			public void run() {
				// TODO Auto-generated method stub
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(selectedFile));
					txtSqlArea.clear(); //텍스트를 쓰기전 초기화
					while(true) {
						String fileStr= reader.readLine();
						
						if(fileStr ==null) {
							break;
						}
						
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								txtSqlArea.appendText(fileStr + "\n");
								
							}
						});
						
						
					}
					
					
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}finally {
					if(reader !=null)
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
				
				
			}};
			
			thread.start(); // 스레드 시작
			
		}
		
	}
	public void handleSave(ActionEvent e) {
		//파일 저장
		saveFile(true);

	}
	
	public void handleAnotherSave(ActionEvent e) {
		//다른이름으로 파일 저장
		saveFile(false);
	}
	
	public void saveFile(boolean saveCheck) {
		FileChooser fileChooser = new FileChooser();
		
		//저장 가능한 파일 형식 설정
		fileChooser.getExtensionFilters().add(new ExtensionFilter("SQL File", "*.sql"));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("All File", "*.*"));
		
		if(primaryStage.getTitle().equals("jSqlDeveloper") || saveCheck==false) { //현재 열려있는 파일이 없다면 filechooser창을 연다. 열려있는창이 있다면 filechooser를 열지 않음
			 
			try {
			selectedFile = fileChooser.showSaveDialog(primaryStage);
			
			primaryStage.setTitle("jSqlDeveloper - "+selectedFile.getPath());
			}catch(NullPointerException e1) {
				
			}
		}
		
		if(selectedFile !=null) {
			PrintWriter writer = null;
			
			try {
				writer = new PrintWriter(new FileWriter(selectedFile), true); //autoflush 설정
				
				writer.print(txtSqlArea.getText());
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}finally {
				if(writer !=null)writer.close();
			}
			
		}
	}
	
	public void handleComboBox(ActionEvent e) {
		
		switch(comboColor.getSelectionModel().getSelectedIndex()) {
			case 0: //black
				txtSqlArea.setStyle("-fx-text-fill:black"); break;
			case 1: //green
				txtSqlArea.setStyle("-fx-text-fill:green"); break;
			case 2: //yellow
				txtSqlArea.setStyle("-fx-text-fill:yellow"); break;
			case 3: // blue
				txtSqlArea.setStyle("-fx-text-fill:blue"); break;
			case 4: // magenta
				txtSqlArea.setStyle("-fx-text-fill:magenta"); break;
			case 5: // red
				txtSqlArea.setStyle("-fx-text-fill:red"); break;
			default: // 디폴트 색은 블랙
				txtSqlArea.setStyle("-fx-text-fill:black"); break;
		}
	}
	
	public void handleRunSQL(ActionEvent e) throws Exception {
		String sql="";
		
		tabView.getColumns().clear();
		tabView.getItems().clear();
		
		if(conn==null) {
			DBConnect(e);
		}else {
			try {
				pstm = conn.createStatement();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
			
		conn.setAutoCommit(false); // 오토커밋 금지
		int nowCursor =txtSqlArea.getCaretPosition(); //현재 커서 위치		
		int prevdata = txtSqlArea.getText().lastIndexOf(";", nowCursor-1); //이전 ;위치
		int nextdata = txtSqlArea.getText().indexOf(";",nowCursor); // 다음 ; 위치
		
		//정규표현식으로 알리아스에 세미콜론이 들어가있는지 확인
		Pattern pattern = Pattern.compile("([\'][\\S]*[;]+[\\S]*[\'])|([\"][\\S]*[;]+[\\S]*[\"])");
		Matcher match = pattern.matcher(txtSqlArea.getText());
		ArrayList<Integer> startPatternList = new ArrayList<>();
		ArrayList<Integer> endPatternList = new ArrayList<>();
		
		startPatternList.clear(); // 버튼을 누를때마다 리스트 초기화
		endPatternList.clear();
		
		while(match.find()) {
			startPatternList.add(match.start());
			endPatternList.add(match.end());
		}
		//for문 하나에 패턴 list 두개를 앞뒤로 각각 돌릴것이기 때문에 반대로 돌릴 변수 생성 
		int j= startPatternList.size()-1;
		
		for(int i=0;i<startPatternList.size();i++) {
			
			if(j<0) {
				break;
			}
			
			
			if(prevdata>=startPatternList.get(j) && prevdata <= endPatternList.get(j)) {
				prevdata = txtSqlArea.getText().lastIndexOf(";",startPatternList.get(j));
			}
			if(nextdata>=startPatternList.get(i) && nextdata<=endPatternList.get(i)) {
				nextdata = txtSqlArea.getText().indexOf(";",endPatternList.get(i));
			}
			j--;
			
		}
		
		
		if(prevdata==-1) { //값을 찾지 못하면 -1값이 나오기때문에 0과 최대값으로 바꿔줌
			prevdata=0;
		}
		if(nextdata==-1) {
			nextdata=txtSqlArea.getLength();
		}
		//이전 위치부터 이후 위치 범위까지 텍스트를 설정	
		
		
		if(txtSqlArea.getSelectedText().isEmpty()) { // 드래그로 선택된 값이 없으면 ? 
			sql = txtSqlArea.getText().substring(prevdata, nextdata);
			if(sql.startsWith(";")) {
			sql = sql.substring(1);
			}
			
		}else { // 드래그된 값이 있으면
			
			sql=txtSqlArea.getSelectedText();
//			if(sql.trim().endsWith(";")) {
//			sql=sql.substring(0, sql.lastIndexOf(";",sql.length() )); //sql범위에서 ;를 찾아서 0범위부터 쿼리 부분만 잘라냄
//			}
		}
	
		try {
			
			if(sql.trim().toUpperCase().startsWith("SELECT")) { //if select문일경우
			
				stackPane.getChildren().clear();
				stackPane.getChildren().add(tabView);
				rs=pstm.executeQuery(sql); // 쿼리 실행
			ResultSetMetaData rsmd = rs.getMetaData();
				
			 for(int i = 1; i<=rsmd.getColumnCount(); i++) {
				 
				 String columnName = rsmd.getColumnName(i);
				 
				 TableColumn<HashMap<String, Object>, Object> tableColumn = new TableColumn<>(columnName);
				 
				 tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<HashMap<String,Object>,Object>, ObservableValue<Object>>() {
					
					@Override
					public ObservableValue<Object> call(CellDataFeatures<HashMap<String, Object>, Object> param) {
						// TODO Auto-generated method stub
						return new SimpleObjectProperty<>(param.getValue().get(columnName));
					}
				});
				 tabView.getColumns().add(tableColumn);
				
			 }
		
			 
			while(rs.next()) { // while rs값을 출력
				HashMap<String, Object> item = new HashMap<>();
				for(int i = 1; i<=rsmd.getColumnCount(); i++) { // for rsmd의 갯수만큼 for문 
					
					item.put(rsmd.getColumnName(i), rs.getObject(rsmd.getColumnName(i)));
					 
					if(item.get(rsmd.getColumnName(i))==null) {
						item.put(rsmd.getColumnName(i), "(null)"); //값이 null일경우 컬럼에 null 표시
					}
				
				} // end for
				
				
				tabView.getItems().add(item);
				
			} // end while
			
			
			} else{ // end if else
				
				
				
				stackPane.getChildren().clear();
				stackPane.getChildren().add(txtResult);
				if(sql.trim().toUpperCase().startsWith("UPDATE")) {
					int count = pstm.executeUpdate(sql); // select가 아닌 sql 실행
					txtResult.setText(count+"행이 업데이트 되었습니다.");
				}else if(sql.trim().toUpperCase().startsWith("DELETE")) {
					int count = pstm.executeUpdate(sql); // select가 아닌 sql 실행
					txtResult.setText(count+"행이 삭제 되었습니다.");
				}else if(sql.trim().toUpperCase().startsWith("INSERT")) {
					int count = pstm.executeUpdate(sql); // select가 아닌 sql 실행
					txtResult.setText(count+"행이 삽입 되었습니다.");
				}else {
					pstm.execute(sql);
				txtResult.setText("명령이 성공적으로 실행되었습니다.");
				}
				// 아랫부분은 drop이나 create 실행시 좌측 리스트뷰에 갱신내역을 바로 띄워주는 구간
				rs = pstm.executeQuery("select table_name from user_tables order by 1");
				listView.getItems().clear();
				while(rs.next()) {
					listView.getItems().add(rs.getString(1));
				}
			}
		} catch (SQLException e1) {
			stackPane.getChildren().clear();
			stackPane.getChildren().add(txtResult);
			txtResult.setText(e1.toString());
		}
		
		
		}
	}
	
	
	
	public void DBConnect(ActionEvent e) throws IOException { //connect버튼
		String sql="select table_name from user_tables order by 1";
		Stage dialog =new Stage();
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(primaryStage);
		dialog.setTitle("DB Connection");
		AnchorPane root = FXMLLoader.load(getClass().getResource("ConnectDB.fxml"));
		
		
		TextField txtId = (TextField) root.lookup("#txtId");
		PasswordField txtPwd = (PasswordField) root.lookup("#txtPwd");
		TextField txtHost = (TextField) root.lookup("#txtHost");
		TextField txtPort = (TextField) root.lookup("#txtPort");
		TextField txtSid = (TextField) root.lookup("#txtSid");
		Button btnConnect = (Button) root.lookup("#btnConnect");
		Button btnClose = (Button) root.lookup("#btnClose");
		Label txtStatus = (Label) root.lookup("#txtStatus");
		
		
		if(conn==null) { // 동시에 재접속 불가능
			btnConnect.setDisable(false);
		btnConnect.setOnAction((event)->{//dbconnect 버튼
			
			
			
			try {
				
				conn = DBConnection.getConnection(txtId.getText(), txtPwd.getText(),
						txtHost.getText(), Integer.parseInt(txtPort.getText()), txtSid.getText());

				//id pwd ip port sid값을 받는다.
				dialog.close();
				pstm = conn.createStatement();
				rs = pstm.executeQuery(sql);
				while(rs.next()) {
					listView.getItems().add(rs.getString(1));
				}
				
			} catch (SQLException e1) {
				
				txtStatus.setText("error : "+e1.getMessage());
				
			} catch (NumberFormatException e1) {
				
				txtStatus.setText("error : 포트번호가 올바르지 않습니다.");
			} catch (ClassNotFoundException e1) {
				
				txtStatus.setText("error : "+e1.getMessage());
			}
			

		
		});//dialog close
		

		
		}else {
			btnConnect.setDisable(true);
		}
		
		btnClose.setOnAction(event->dialog.close()); //취소버튼.
		
		Scene scene = new Scene(root);
		dialog.setScene(scene);
		dialog.show();
		
		
	}
	
	public void handleConnectionClose(ActionEvent e) throws SQLException, IOException { // 현재 커넥션 종료
		
		
		if(rs !=null)rs.close(); // resultset 종료
		if(pstm !=null)pstm.close(); //statement 종료
		if(conn !=null) { // 접속 종료시 
			conn.close();
			conn = null; //conn값을 null값으로 돌려놓음
			listView.getItems().clear(); //각종 뷰컨트롤의 아이템값 초기화
			tabView.getItems().clear();
			tabView.getColumns().clear();
			txtResult.setText("");
			stackPane.getChildren().clear(); //하단부 스택페인의 자식 컨테이너 삭제
			handleAlert(endForConnection); //접속종료 메시지
		}else {
			handleAlert(noUser); //접속중인 유저가 없음
						
		}
		
	}
	public void handleAlert(final int flag) throws IOException {
		Popup popUp = new Popup();
		HBox root = FXMLLoader.load(getClass().getResource("Popup.fxml"));
		Label label = (Label) root.lookup("#txtPopup");
		
		if(flag==1) { //텍스트 폰트
			label.setText("숫자만 입력해주세요.");
		}else if(flag==2) { //접속이 종료되었습니다.
			label.setText("접속이 종료되었습니다.");
		}else if(flag==3) {//접속중인 유저가 없습니다.
			label.setText("접속중인 유저가 없습니다.");
		}else if(flag==4) {
			label.setText("폰트 크기는 150까지만 가능합니다.");
		}
		popUp.getContent().add(root);
		popUp.show(primaryStage);
		popUp.setAutoHide(true);
		
	}
	
	
	public void handleClose(ActionEvent e) throws SQLException { // close버튼
		if(rs !=null)rs.close(); // resultset 종료
		if(pstm !=null)pstm.close(); //statement 종료
		if(conn !=null)conn.close(); // connection 해제
		Platform.exit();
	}
}
