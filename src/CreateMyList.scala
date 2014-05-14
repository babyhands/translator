import scala.swing._
import BorderPanel.Position._
import scala.swing.event._
import TabbedPane._
import javax.swing.ImageIcon
import javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE
import scala.io.Source
import scala.swing.RadioMenuItem
import scala.util.Random


object CreateMyList {
      
	case class Spanglish(spanish: String, english: String)
    var db = List[Spanglish]()  

    
    def main(args: Array[String]) = {
    val userEnglishField = new TextField("")
    val userSpanishField = new TextField("")
    
    val tabPane = new TabbedPane{
      
      ///////// start of Creating List tab ////////// 
      pages += new Page("Create My List", new BorderPanel {
      val addButton = new Button{
        text = "Add to list!"
      } 
      
      val clearButton = new Button{
        text = "Clear Your List!" 
      }
      
      val openButton = new Button {
        text = "Choose existing list"
      }
      
      val saveButton = new Button {
        text = "Save Your List"
      }
      
      var clearLabel = new Label {
        listenTo(clearButton)
        reactions += {
          case ButtonClicked(clearButton) =>
            db = List[Spanglish]() 
        } 
      }
      
     var addLabel = new Label {
        listenTo(addButton, userSpanishField, userEnglishField)
        reactions += {
          case ButtonClicked(addButton) => {
            if (userEnglishField.text.length > 0 && userSpanishField.text.length > 0) {
              val addSpan = new Spanglish(userSpanishField.text, userEnglishField.text)
              db = db:+addSpan
              userEnglishField.text = ""
              userSpanishField.text = ""
            }
          }
        }
     }     
     
    val database = new ListView(db.map(_.spanish)) {  
      listData = Seq(" ")
      listenTo(addButton, clearButton, addLabel, clearLabel, openButton, saveButton) 
      reactions += {
        case ButtonClicked(`addButton`) =>  //partial function only listen to SelectionChanged
          listData = db.map(_.spanish)
        case ButtonClicked(`clearButton`)  =>  //partial function only listen to SelectionChanged
          listData = Seq(" ")
        case ButtonClicked(`openButton`) =>
          openFile
          listData = db.map(_.spanish)
        case ButtonClicked(`saveButton`) =>
          saveFile
      	}
      
      }
    
    //start of CreateList layout
    border = Swing.EmptyBorder(0, 30, 10, 30)
    layout += new GridPanel(3,1) {
      contents += new BorderPanel{
        border = Swing.EmptyBorder(15, 0, 0, 0)
        layout += new Label {
          text = "Enter your Spanish Word  "
        } -> West 
        layout += userSpanishField -> Center
      }
      contents += new BorderPanel{
        border = Swing.EmptyBorder(0, 0, 15, 0)
        layout += new Label {
          text = "Enter your English Word  "
        } -> West
        layout += userEnglishField -> Center
        }
      contents += new GridPanel(2,2) {
        border = Swing.EmptyBorder(0,10,0,10)
        contents += addButton
        contents += clearButton
        contents += saveButton
        contents += openButton
      }
      } -> Center
      layout += new ScrollPane(database) -> South
      })
      
   ///////// start of Study List tab //////////  
   pages += new Page("Study My List", new BorderPanel {
     
    val theFrame = new BorderPanel() 
    
    val spanishField = new Label("")
    val englishField = new Label("") 
     
    val loadButton = new Button {
      text = "Load My List"
      spanishField.text = ""
      englishField.text = ""
    } 
        
    val database = new ListView(db.map(_.spanish)) {
      listData = Seq("Please load your list to view")
      listenTo(loadButton, selection) 
      reactions += {
        case ButtonClicked(_) =>  //partial function only listen to ButtonClikced
          if (db.isEmpty){ // if user does not initially create a list
            spanishField.text = " "
            englishField.text = " " 
            listData = Seq("There is no list to load")
          }
          else listData = db.map(_.spanish)  // loads list
        case event: SelectionChanged =>  //partial function only listen to SelectionChanged
          if (db.isEmpty){ // if user clears list
            spanishField.text = " "
            englishField.text = " " 
            listData = Seq("There is no list to load")
          }
          else {
          val theDB = db(selection.leadIndex)
          spanishField.text = theDB.spanish
          englishField.text = theDB.english  
          }
       }
     }  
    
      // beginning of layout for study list page
        border = Swing.EmptyBorder(10, 30, 10, 30)
        layout += new BorderPanel {
          layout += new GridPanel(3,1){
            contents += new Label("Please select a Spanish word to translate.")
            contents += new BorderPanel{
              border = Swing.EmptyBorder(10, 50, 10, 50)
              layout += new Label("Spanish: ") -> West
              layout += spanishField -> Center
             }
            contents += new BorderPanel{
              border = Swing.EmptyBorder(10, 50, 10, 50)
              layout += new Label("English: ") -> West
              layout += englishField -> Center
            }           
          } -> North
        } -> Center 
        layout += new BorderPanel {
          layout += new GridPanel (1,1){
            border = Swing.EmptyBorder(10, 50, 10, 50)
            contents += loadButton
          } -> North
          layout += new ScrollPane(database) {
          }-> Center
        } -> South
        
    }) // end of study list page      
    
      //page for multiple choice quiz
    pages += new Page("Quiz", new BorderPanel {
      val quizButton = new Button{
        text = "Start Quiz"
      }
      
      var clearLabel = new Label {
        listenTo(quizButton)
        reactions += {
          case ButtonClicked(quizButton) =>
            quiz 
        } 
      }
      
      layout += new BorderPanel{
        layout += new GridPanel(1,1){
          border = Swing.EmptyBorder(150, 100, 150, 100)
          contents += quizButton
        }-> Center
      }-> Center
    	})     
    }
  
    
    val ui: Panel = new BorderPanel {
      layout(tabPane) = BorderPanel.Position.Center
    }
    
    val mainFrame = new MainFrame {
    contents = ui
    title = "Translator v2.0 | Managing Personal List"
    centerOnScreen
    size = new Dimension(450,400)
    peer.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)
    override def closeOperation() { close() }
    }
    mainFrame.open
  }
	  //function used to choose and open a text file with
  //a list in it!
  def openFile {
    val chooser = new FileChooser(null)
    chooser.showOpenDialog(null)
    val src = Source.fromFile(chooser.selectedFile)  //setting src to the selected file
    val lines = src.getLines
    while (lines.hasNext){  
    val spanWord = lines.next
    val engWord = lines.next
    val use = new Spanglish(spanWord, engWord)
    db = db:+use
    }
  src.close()
  }
  
    //function used to save your current list to a text file
  def saveFile{
    val chooser = new FileChooser(null)
    chooser.showSaveDialog(null)
    val pw = new java.io.PrintWriter(chooser.selectedFile) //makes a printwriter to the selected file
    var index = 0
    while (index < db.length){ 
        var spanObj = db(index)
        var spanWord = spanObj.spanish
    	var engWord = spanObj.english
    	pw.println(spanWord)
    	pw.println(engWord)
    	index = index+1
  }
    pw.close()
  }
  
  //this function pops up the windows and lets you do two different quizes
 import scala.util.Random._
  def quiz{    
    //this is where all of the UI goes for the quiz
    val quizPane = new TabbedPane{
      
      //multiple choice page
      pages += new Page("Multiple Choice", new BorderPanel {
        
                var start = db.head
    
        // displays spanish word to answer
        val spanishField = new Label(db.head.spanish) 
    
        // holds english word to compare user answer with
        val englishField = new Label(db.head.english)  
        
        def check (x : Spanglish) = {
          var newWord = db(Random.nextInt(db.length))
          while(x == newWord){
            newWord = db(Random.nextInt(db.length))
          }
          newWord
        }
        
        def check2 (x : Spanglish, x2 : Spanglish ) = {
          var newWord = db(Random.nextInt(db.length))
          while(x == newWord | x2 == newWord){
            newWord = db(Random.nextInt(db.length))
          }
          newWord
        }
        
        var result = check(start)
        var result2 = check2(start, result)
        
        val questionField = new Label("What is the English translation for " + spanishField.text)
        
        var possibleAnswers = List(englishField.text, result.english, result2.english)
        // shuffles possible answers
        var shuffleList = shuffle(possibleAnswers)

        var mutex = new ButtonGroup
        var answer1 = new RadioButton{
          text = shuffleList.head  
        }
        var answer2 = new RadioButton{
          text = shuffleList.tail.head
        }   
        var answer3 = new RadioButton{
          text = shuffleList.last
        }
        var invisibleRadioButton = new RadioButton {
          this.visible = false
        }
        var radios = List(answer1, answer2, answer3, invisibleRadioButton)
        
        mutex.buttons ++= radios 
        
        mutex.select(invisibleRadioButton)
        
        val radioButtons = new BoxPanel(Orientation.Vertical) {
          contents ++= radios
        }
        
        val submitButton = new Button {
          text = "Submit Answer"
        }
        

        var dbCopy = db
        var correctTotal = 0
        
        def answerLabel = new Label {
          text = ""
          listenTo(submitButton, radioButtons)
          reactions += {
            case ButtonClicked(submitButton) =>
              // if user correctly answers question
              if(mutex.selected.get.text == englishField.text){
                correctTotal += 1
                text = ("Excellente!   " + spanishField.text + " -> " + englishField.text)
                if (dbCopy.tail.isEmpty){
                    text = ("Excellente!   " + spanishField.text + " -> " + englishField.text)
                    text = ("You got " + correctTotal + "/" + db.length )
                    submitButton.visible = false         
                }
                else {  
                dbCopy = dbCopy.tail
                start = dbCopy.head
                spanishField.text = (dbCopy.head.spanish) 
                englishField.text = (dbCopy.head.english)  

                result = check(start)
                result2 = check2(start, result)
                
                possibleAnswers = List(englishField.text, result.english, result2.english)
                shuffleList = shuffle(possibleAnswers)                
                answer1.text = shuffleList.head
                answer2.text = shuffleList.tail.head
                answer3.text = shuffleList.last
                mutex.select(invisibleRadioButton)      
                questionField.text = ("What is the English translation for " + spanishField.text)
                }
              }
              // else user incorrectly answers question
              else {
                text = ("Lo siento, your are incorrect!   " + spanishField.text + " -> " + englishField.text)
                if(dbCopy.tail.isEmpty){
                  text = ("You got " + correctTotal + "/" + db.length)
                  submitButton.visible = false
                }
                else {
                  dbCopy = dbCopy.tail
                  start = dbCopy.head
                  spanishField.text = (dbCopy.head.spanish) 
                  englishField.text = (dbCopy.head.english) 
                  
                  result = check(start)
                  result2 = check2(start, result)
                
                  possibleAnswers = List(englishField.text, result.english, result2.english)
                  shuffleList = shuffle(possibleAnswers)                
                  answer1.text = shuffleList.head
                  answer2.text = shuffleList.tail.head
                  answer3.text = shuffleList.last
                  mutex.select(invisibleRadioButton)      
                  questionField.text = ("What is the English translation for " + spanishField.text)
                  }
                }
              }
          }


          border = Swing.EmptyBorder(30, 30, 30, 30)
          layout += questionField -> North
          layout += new BorderPanel{
            border = Swing.EmptyBorder(30, 0, 30, 0)
            layout += radioButtons -> Center
            layout += answerLabel -> South
          } -> Center
          layout += submitButton -> South
      })
      
      //fill in the blank page
      pages += new Page("Fill in the Blank", new BorderPanel {
        
                // displays spanish word to answer
        val spanishField = new Label(db.head.spanish) 
    
        // holds english word to compare user answer with
        val englishField = new TextField(db.head.english)   
    
        // user input field to answer question
        val userAnswerField = new TextField("")
        var correctTotal = 0

        // creates a button to check answer
        val answerButton = new Button {
          text = "Check Answer"  
        }
        
        // creates a button to restart test
        val restartButton = new Button {
          text = "Restart Test"
        }
        
        var dbCopy = db
        
        // creates a label will display if user correctly or incorrectly answered question
        // executes when answer button when clicked
        def answerLabel = new Label {
          listenTo(answerButton)
          reactions += {
            case ButtonClicked(_) =>
              // if user correctly answers question
              if(englishField.text == userAnswerField.text){
                correctTotal += 1
                text = ("Excellente!   " + spanishField.text + " -> " + englishField.text)
                if (dbCopy.tail.isEmpty){
                  text = ("You got " + correctTotal + "/" + db.length )
                  answerButton.visible = false
                }
                else {
                  dbCopy = dbCopy.tail
                  spanishField.text = dbCopy.head.spanish
                  englishField.text = dbCopy.head.english
                  userAnswerField.text = ""
                }
              }
              // else user incorrectly answers question
              else {
                text = ("Lo siento, your are incorrect!   " + spanishField.text + " -> " + englishField.text)
                if(dbCopy.tail.isEmpty){
                  text = ("You got " + correctTotal + "/" + db.length)
                  answerButton.visible = false
                 }
                 else {
                   dbCopy = dbCopy.tail
                   spanishField.text = dbCopy.head.spanish
                   englishField.text = dbCopy.head.english
                   userAnswerField.text = ""
                 }
               }
             }
          }
      
      // beginning of layout  
      layout += new GridPanel(5,1) {
        border = Swing.EmptyBorder(20, 20, 50, 20)
        contents += new Label {
          text = "Translate the Spanish word into English: "
        }
        contents += spanishField
        contents += new BorderPanel {
          border = Swing.EmptyBorder(15, 0, 15, 0)
          layout += new Label{
            text = "My Answer: "
          } -> West
          layout += userAnswerField -> Center
        }
        contents += answerLabel
        contents += new GridPanel(1,1) {
          border = Swing.EmptyBorder(10, 40, 10, 40)
          contents += answerButton  
        }
      } -> Center
      })
      
    }
    //doing this so I can add it to the mainframe contents
    val gui: Panel = new BorderPanel {
      layout(quizPane) = BorderPanel.Position.Center
    }
     val mainframe2 = new MainFrame{
    contents = gui 
    title = "Quiz Section"
    centerOnScreen
    size = new Dimension(450,400)
    peer.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)
    override def closeOperation() { close() }
    }
   mainframe2.open
  }
  
  
  
  
  
  
}
