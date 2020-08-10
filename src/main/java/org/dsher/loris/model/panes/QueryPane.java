package org.dsher.loris.model.panes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class QueryPane extends GridPane {

	final ProgressBar pb = new ProgressBar();

	private String title;

	public QueryPane(String title) {
		super();
		this.title = title;
	}

	public QueryPane build() {
		this.setAlignment(Pos.CENTER);
		this.setHgap(10);
		this.setVgap(10);
		this.setPadding(new Insets(25, 25, 25, 25));

		Text sceneTitle = new Text(this.title);
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		this.add(sceneTitle, 0, 0);

		pb.setProgress(0);
		pb.setPrefWidth(1000);

		this.add(pb, 0, 3);
		return this;
	}

	public void updateProgressBar(double progress) {
		pb.setProgress(progress);
	}

}
