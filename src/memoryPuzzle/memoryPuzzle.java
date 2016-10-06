package memoryPuzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class memoryPuzzle extends Application{
	private int NUM_PAIRS = 18;
	private int NUM_ROW = 6;
	private Tile selected = null;
	private int clickCount = 2;
	private Pane root = new Pane();
	private List<Tile> tiles = new ArrayList<>();
	private List<Tile> finishTiles = new ArrayList<>();
	private Scene scene;
	private IntegerProperty winCount = new SimpleIntegerProperty();
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println(winCount);
		scene = new Scene(createContent());
		primaryStage.setTitle("Puzzle- Let's Begin"); 

		primaryStage.setResizable(false);
		primaryStage.setScene(scene);		
		primaryStage.show();
	}

	private Parent createContent() {
		root.setPrefSize(600, 600);	
		initializeTile();
		return root;
	}
	
	private void initializeTile() {
		char c = 'A';
		for(int i = 0; i< NUM_PAIRS ; i++) {
			for(int j =0; j< (NUM_ROW*NUM_ROW/NUM_PAIRS); j++){
			tiles.add(new Tile(String.valueOf(c)));
			}
			c++;
		}
		
		Collections.shuffle(tiles);
		for(int j = 0; j< tiles.size(); j++) {
			Tile tile = tiles.get(j);
			tile.setTranslateX(100*(j%NUM_ROW));
			tile.setTranslateY(100*(j/NUM_ROW));
			root.getChildren().add(tile);
		}
		
	}

	private class Tile extends StackPane{
		Text text = new Text();
		
		public Tile(String value) {
			Rectangle border = new Rectangle(100,100);
			border.setFill(Color.DARKSALMON);
			border.setStroke(Color.BLACK);
			text.setFont(Font.font(36));
			text.setText(value);
			text.setFill(Color.CYAN);
			setAlignment(Pos.CENTER);
			getChildren().addAll(border,text);	
			
				setOnMouseClicked(event -> {
					// Prevent double clicking the same tile
				if(isOpen() || clickCount == 0) return;
						clickCount--;
				if(selected == null) {
					selected = this;	
					Open(() -> {});
				} else {
					Open(()-> {
						if(!haveSameValue(selected)){
							selected.Close();
							this.Close();
						} else {
							addFinishTile();
							if(isGameEnded()) {
								System.out.println("ENDED");
								scene.setRoot(showWinningStage());
								//RESET
								for(Tile tile :finishTiles) {
									tile.Close();
								}
								finishTiles.clear();
							}
						}
						selected = null;
						clickCount =2;
					});
				} 					
			});
			
			Close();
		}

		private void Open(Runnable action) {
			FadeTransition ft = new FadeTransition(Duration.seconds(0.5), text);
			ft.setToValue(1);
			ft.setOnFinished(e -> action.run());
			ft.play();
		}
		private Parent showWinningStage() {
			Pane root1 = new Pane();
			BackgroundImage bi = new BackgroundImage(new Image("memoryPuzzle/resource/win.png", 600, 600, false, true),
					BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
			
			root1.setBackground(new Background(bi));
			scene.setOnMouseClicked(e -> {
				scene.setRoot(root);

			});
			return root1;	
		}
		
		private void addFinishTile() {
			finishTiles.add(selected);
			finishTiles.add(this);			
		}
		private boolean isGameEnded(){	
			return finishTiles.size() == NUM_ROW*NUM_ROW;
		} 
		private boolean isOpen() {
			return text.getOpacity() > 0;
		}

		private boolean haveSameValue(Tile other) {
			return text.getText().equals(other.text.getText());
		}

		private void Close() {
		FadeTransition ft = new FadeTransition(Duration.seconds(0.5), text);
		ft.setToValue(0);
		ft.play();
		}
	}

}
