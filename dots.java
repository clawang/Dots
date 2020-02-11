int w = 6;
int h = 6;
int bs = 100;
int marginTop = 100;
int marginBottom = 100;
int diameter = 40;
int[][] board = new int[h][w];
int score = 0;
int moves = 30;
int numColors=5;
int bestScore = 0;

int state=1;
// state 0 = how to play, state 1 = start screen, state 2 = easy, state 3 = normal, state 4 = hard, state 5 = dead

boolean[][] selected=new boolean[h][w];
boolean hasSquare=false;
int colorDragged = 0;
boolean isDragging = false;
PFont font;
PImage startscreen;
PImage howtoplay;
PImage gameover;
PImage movesImg;
PImage scoreImg;
PImage pressr;

ArrayList<Integer> xDragged = new ArrayList<Integer>();
ArrayList<Integer> yDragged = new ArrayList<Integer>();

void setup() {
  //size(w*bs, marginTop+h*bs+marginBottom);
  size(600, 800);

  noStroke();
  smooth();
  //font = loadFont("JosefinSans-Bold-30.vlw");
  font = createFont("JosefinSans-Bold.ttf", 50);
  startscreen = loadImage("http://i.imgur.com/lxMiilE.jpg");
  howtoplay = loadImage("http://i.imgur.com/4wWyony.jpg");
  gameover = loadImage("http://i.imgur.com/DdFGvWv.jpg");
  movesImg = loadImage("http://i.imgur.com/QkZmobP.jpg");
  scoreImg = loadImage("http://i.imgur.com/7RFLWcI.jpg");
  pressr = loadImage("http://i.imgur.com/sUffGsz.jpg");
  rectMode(CENTER);
  textFont(font, 30);
  textSize(30); //specifies size of text
}

void draw() {
  background(255);
  if (state==0) howToPlay();
  if (state==1) startScreen();
  if (state==2) {
    gameState();
  }
  if (state==3) {
    gameState();
  }
  if (state==4) {
    gameState();
  }
  if (state==5) {
    gameOver();
  }
}

void gameOver() {
  if (score > bestScore) {
    bestScore = score;
  }
  //  background(gameover);
  image(gameover, 0, 0);
  textSize(50);
  textAlign(CENTER);
  fill(24, 178, 156);
  text(score, 165, 450);
  text(bestScore, 445, 450);
  if (key=='r') {
    state = 1;
    moves = 30;
    score = 0;
  }
}

void startScreen() {
  //  background(startscreen);
  imageMode(CORNER);
  image(startscreen, 0, 0);
  if (key=='1') {
    numColors = 4;
    state = 2;
  }
  if (key=='2') {
    numColors = 5;
    state = 2;
  }
  if (key=='3') {
    numColors = 6;
    state = 2;
  }
  if (key=='4') {
    howToPlay();
  }
  for (int row = 0; row < h; row++) {
    for (int c = 0; c < w; c++) {
      board[row][c] = int(random(0, numColors));
    }
  }
}

void howToPlay() {
  image(howtoplay, 0, 0);
  if (key=='b') {
    state = 1;
  }
}

void gameState() {
  if (moves == 0) {
    state = 5;
  }
  for (int i = 0 ; i < xDragged.size(); i++) {
    int row = yDragged.get(i);
    int c = xDragged.get(i);
    selected[row][c]=true;
  }

  imageMode(CENTER);
  image(movesImg, 150, 70);
  image(scoreImg, 400, 70);
  imageMode(CENTER);
  image(pressr, 300, 750);
  imageMode(CORNER);

  if (isDragging) {
    selectStuff();
  }

  drawEdges();
  drawHiLiting();
  fill(0);
  textSize(30);
  textAlign(LEFT);
  text(moves, 200, 80);
  text(score, 445, 80);
  //  text("to restart press r", w*bs/2, marginTop+h*bs+marginBottom-50);
  if (key=='r') {
    state = 1;
    moves = 30;
    score = 0;
  }
}

void setBoard() {
  xDragged.clear();
  yDragged.clear();
  for (int row = 0; row < h; row++) {
    for (int c = 0; c < w; c++) {
      board[row][c] = int(random(0, numColors));
    }
  }
}

color col(int v) {
  if (v==0) return color(243, 148, 54); //orange
  if (v==1) return color(152, 201, 133); //green
  if (v==2) return color(85, 191, 180); //blue
  if (v==3) return color(241, 117, 118); //coral
  if (v==4) return color(214, 155, 172); //pink
  if (v==5) return color(248, 220, 84); //yellow
  return 0;
}

void selectStuff() { //detects if on circle
  int prevRow = yDragged.get(yDragged.size()-1);
  int prevCol = xDragged.get(xDragged.size()-1);
  int c=prevCol;
  int row=prevRow;
  if (mouseY>height/2-300 && mouseY<height/2+300) {
    c = int(mouseX / bs);
    row = int((mouseY - marginTop) / bs);
  }

  if (xDragged.size()>=2 && xDragged.get(xDragged.size()-2)==c && yDragged.get(yDragged.size()-2)==row) {
    hasSquare=false;
    selected[yDragged.get(yDragged.size()-1)][xDragged.get(xDragged.size()-1)]=false;
    xDragged.remove(xDragged.size()-1);
    yDragged.remove(yDragged.size()-1);
  }
  else if (dist(mouseX, mouseY, c*bs+bs/2, marginTop+row*bs+bs/2) < diameter / 1) {

    if ((abs(row-prevRow)==1 && prevCol == c  
      || abs(c-prevCol)==1 && row==prevRow) && board[row][c]==colorDragged) {
      if (!(row==prevRow && c==prevCol)) {
        if (selected[row][c] && !hasSquare) {
          selected[row][c]=true;
          xDragged.add(c);
          yDragged.add(row);
          hasSquare=true;
        }
        else if (selected[row][c] && hasSquare) {
        }
        else {
          selected[row][c]=true;
          xDragged.add(c);
          yDragged.add(row);
        }
      }
    }
  }
  drawRectangles(row, c);
}

void drawRectangles(int row, int c) {
  int rectWidth=xDragged.size()*50;
  if (hasSquare) {
    rectWidth=width;
  }
  fill(col(colorDragged)); //creates lines on top & bottom of screen
  rect(width/2, 7, rectWidth, 15);
  rect(width/2, marginTop+h*bs+marginBottom - 7, rectWidth, 15);
  if (hasSquare) {
    rectMode(CORNER);
    fill(col(colorDragged));
    rect(0, 0, 10, marginTop+h*bs+marginBottom);
    rect(w*bs - 10, 0, 10, marginTop+h*bs+marginBottom);
    fill(col(colorDragged), 50);
    rect(0, 0, w*bs, marginTop+h*bs+marginBottom);
    rectMode(CENTER);
  }
}

void drawEdges() { //draws lines between dots
  if (xDragged.size()>1) {
    for (int i=1;i<xDragged.size();i++) {
      int row=yDragged.get(i);
      int c=xDragged.get(i);
      int prevRow=yDragged.get(i-1);
      int prevCol=xDragged.get(i-1);
      int linex = c*bs+bs/2;
      int liney =marginTop+row*bs+bs/2;
      int prevLinex = prevCol*bs+bs/2;
      int prevLiney =marginTop+prevRow*bs+bs/2;
      stroke(col(board[row][c]));
      strokeWeight(10);
      line(linex, liney, prevLinex, prevLiney);
      noStroke();
    }
  }
}

void drawHiLiting() { //draws transparent circles around dots
  for (int row = 0; row < h; row++) {
    for (int c = 0; c < w; c++) {
      fill(col(board[row][c]));
      //      rect(col*bs,row*bs,bs,bs);
      ellipse(c*bs+bs/2, marginTop+row*bs+bs/2, diameter, diameter);
      if (selected[row][c]==true) {
        fill(col(board[row][c]), 50); //bigger transparent dots
        //        if (row==yDragged.get(yDragged.size()-1) && col==xDragged.get(xDragged.size()-1)) fill(0, 255, 255, 50);
        ellipse(c*bs+bs/2, marginTop+row*bs+bs/2, diameter*2, diameter*2);
      }
      if (hasSquare && board[row][c]==colorDragged) {
        fill(col(board[row][c]), 50);
        ellipse(c*bs+bs/2, marginTop+row*bs+bs/2, diameter*2, diameter*2);
      }
      selected[row][c]=false;
    }
  }
}

void mousePressed() {
  if (mouseY>height/2-300 && mouseY<height/2+300) {
    int c = int(mouseX / bs);
    int row = int((mouseY - marginTop) / bs);
    //println(row + " " + col);
    if (dist(mouseX, mouseY, c*bs+bs/2, marginTop+row*bs+bs/2) < diameter / 1) {
      isDragging = true;
      colorDragged = board[row][c];
      xDragged.add(c);
      yDragged.add(row);
      selected[row][c]=true;
      hasSquare=false;
    }
  }
}

void mouseReleased() {
  if (xDragged.size()>1) moves--;
  isDragging = false;
  for (int j=0;j<selected.length;j++) for (int i=0;i<selected[0].length;i++) selected[j][i]=false;
  if (xDragged.size()>=2) {
    for (int i = 0 ; i < xDragged.size(); i++) {
      int row = yDragged.get(i);
      int c = xDragged.get(i);
      board[row][c]=-1; //-1 is black
      score++;
    }

    for (int i=0;i<w;i++) for (int j=0;j<h;j++) {
      if (board[i][j]==colorDragged && hasSquare) { 
        board[i][j]=-1;
        score++;
      }
    }

    for (int i = 0; i < w; i++) {
      for (int j = h - 1; j >= 0; j--) {
        //        selected[i][j]=false;
        //        println("cleared "+i+", "+j);
        if (board[j][i] == -1) { //original black dot
          for (int k = j; k >= 0; k--) {
            if (board[k][i] != -1) { //closest non black dot
              board[j][i] = board[k][i];
              board[k][i] = -1;
              break;
            }
          }
        }
      }
    }

    for (int i=0;i<w;i++) for (int j=0;j<h;j++) {
      if (board[i][j]==-1) board[i][j]=(int)random(0, numColors);
      while (hasSquare && board[i][j]==colorDragged) {
        board[i][j]=(int)random(0, numColors);
      }
    }
    hasSquare=false;
  }
  xDragged.clear();
  yDragged.clear();
}
