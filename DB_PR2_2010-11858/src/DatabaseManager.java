import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DatabaseManager {

	public static final int PRINT_UNIVERSITY = 1;
	public static final int PRINT_STUDENT = 2;
	public static final int INSERT_UNIVERSITY = 3;
	public static final int REMOVE_UNIVERSITY = 4;
	public static final int INSERT_STUDENT = 5;
	public static final int REMOVE_STUDENT = 6;
	public static final int MAKE_APPLICATION = 7;
	public static final int PRINT_APPLIED_STUDENT = 8;
	public static final int PRINT_APPLIED_UNIVERSITY = 9;
	public static final int PRINT_EXPECTED_STUDENT = 10;
	public static final int PRINT_EXPECTED_UNIVERSITY = 11;
	public static final int EXIT = 12;

	public static Connection conn;

	public static void main(String[] args) throws SQLException {

		setUp();

		while (true) {
			selectAction();
			Scanner input = new Scanner(System.in);
			int q = Integer.parseInt(input.nextLine());

			switch (q) {
			case PRINT_UNIVERSITY:
				printUniversity();
				break;
			case PRINT_STUDENT:
				printStudent();
				break;
			case INSERT_UNIVERSITY:
				insertUniversity(input);
				break;
			case REMOVE_UNIVERSITY:
				removeUniversity(input);
				break;
			case INSERT_STUDENT:
				insertStudent(input);
				break;
			case REMOVE_STUDENT:
				removeStudent(input);
				break;
			case MAKE_APPLICATION:
				makeApplication(input);
				break;
			case PRINT_APPLIED_STUDENT:
				printAppliedStudent(input);
				break;
			case PRINT_APPLIED_UNIVERSITY:
				printAppliedUniversity(input);
				break;
			case PRINT_EXPECTED_STUDENT:
				printExpectedStudent(input);
				break;
			case PRINT_EXPECTED_UNIVERSITY:
				printExpectedUniversity(input);
				break;
			case EXIT:
				System.out.println("Bye!");
				System.exit(0);
			default:
				System.out.println("input: " + q);
				System.out.println("Invalid action.");
			}
		}

	}

	public static void setUp() throws SQLException {
		String serverName = "147.46.15.238";
		String dbName = "DB-2010-11858";
		String userName = "DB-2010-11858";
		String password = "DB-2010-11858";
		String url = "jdbc:mariadb://" + serverName + "/" + dbName;
		conn = DriverManager.getConnection(url, userName, password);
	}

	public static void selectAction() {
		System.out.println("============================================================");
		System.out.println("1. print all universities");
		System.out.println("2. print all students");
		System.out.println("3. insert a new university");
		System.out.println("4. remove a university");
		System.out.println("5. insert a new student");
		System.out.println("6. remove a student");
		System.out.println("7. make an application");
		System.out.println("8. print all students who applied for a university");
		System.out.println("9. print all universities a student applied for");
		System.out.println("10. print expected successful applicants of a university");
		System.out.println("11. print universities expected to accept a student");
		System.out.println("12. exit");
		System.out.println("============================================================");
		System.out.print("Select your action: ");
	}

	public static void printUniversity() {
		String sql = "SELECT * FROM university";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			String result = "---------------------------------------------------------------------------\n";
			result += "id\tname\tcapacity\tgroup\tweight\tapplied\n";
			result += "---------------------------------------------------------------------------\n";
			while (rs.next()) {
				int ID = rs.getInt("ID");
				String name = rs.getString("name");
				int capacity = rs.getInt("capacity");
				String group = rs.getString("univ_group");
				float weight = rs.getFloat("weight");
				int applied = getStudentNumber(ID);
				result += (ID + "\t" + name + "\t" + capacity + "\t" + group
						+ "\t" + Math.round(weight*100)/100.0 + "\t" + applied + "\n");
			}
			result += "---------------------------------------------------------------------------\n";
			System.out.print(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
	}

	public static void printStudent() {
		String sql = "SELECT * FROM student";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			String result = "---------------------------------------------------------------------------\n";
			result += "id\tname\tcsat_score\tschool_score\n";
			result += "---------------------------------------------------------------------------\n";
			while (rs.next()) {
				int ID = rs.getInt("ID");
				String name = rs.getString("name");
				int csat_score = rs.getInt("csat_score");
				int school_score = rs.getInt("school_score");
				result += (ID + "\t" + name + "\t" + csat_score + "\t"
						+ school_score + "\n");
			}
			result += "---------------------------------------------------------------------------\n";
			System.out.print(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
	}

	public static void insertUniversity(Scanner input) {
		System.out.print("University name: ");
		String name = input.nextLine();
		if (name.length() > 128)
			name = name.substring(0, 128);
		System.out.print("University capacity: ");
		int capacity = input.nextInt();
		if (capacity <= 0) {
			System.out.println("capacity should be over 0");
			return;
		}
		System.out.print("University group: ");
		String group = input.next();
		if (!groupCheck(group)) {
			System.out.println("Group should be 'A', 'B', or 'C'.");
			return;
		}
		System.out.print("Weight of high school records: ");
		float weight = input.nextFloat();
		if (weight < 0) {
			System.out
					.println("Weight of high school records cannot be negative");
			return;
		}

		String sql1 = "SELECT * FROM university order by ID desc";
		String sql2 = "INSERT INTO university VALUES(?, ?, ?, ?, ?)";
		try {
			PreparedStatement stmt1 = conn.prepareStatement(sql1);
			ResultSet rs1 = stmt1.executeQuery();
			int ID;
			if (rs1.next())
				ID = rs1.getInt("ID");
			else
				ID = 0;
			PreparedStatement stmt2 = conn.prepareStatement(sql2);
			stmt2.setInt(1, ID + 1);
			stmt2.setString(2, name);
			stmt2.setInt(3, capacity);
			stmt2.setString(4, group);
			stmt2.setFloat(5, weight);
			stmt2.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
		System.out.println("A university is successfully inserted");
	}

	public static boolean groupCheck(String group) {
		if (group.equals("A"))
			return true;
		else if (group.equals("B"))
			return true;
		else if (group.equals("C"))
			return true;
		else
			return false;
	}

	public static void removeUniversity(Scanner input) {
		
		System.out.print("University id: ");
		int u_id = input.nextInt();
		String sql1 = "SELECT * FROM university WHERE ID = "+u_id+";";
		String sql2 = "SELECT s_id FROM application WHERE u_id ="+u_id+";";
		PreparedStatement stmt1;
		PreparedStatement stmt2;
		PreparedStatement stmt3;
		PreparedStatement stmt4;
		PreparedStatement stmt5;
		ResultSet rs1;
		ResultSet rs2;
		try {
			stmt1 = conn.prepareStatement(sql1);
			rs1 = stmt1.executeQuery();
			if(!rs1.next())
			{
				System.out.println("University "+u_id+" doesn't exist.");
				return;
			}
			stmt2 = conn.prepareStatement(sql2);
			rs2 = stmt2.executeQuery();
			String group = "apply"+rs1.getString("univ_group");
			int s_id;
			while(rs2.next())
			{
				s_id = rs2.getInt("s_id");
				stmt3 = conn.prepareStatement("UPDATE student set "+group+" = false where ID ="+s_id+";");
				stmt3.executeUpdate();
			}
			
			stmt4 = conn.prepareStatement("DELETE FROM application WHERE u_id = "+u_id+";");
			stmt4.executeUpdate();
			
			stmt5 = conn.prepareStatement("DELETE FROM university WHERE ID = "+u_id+";");
			stmt5.executeUpdate();
			
			System.out.println("A university is successfully deleted");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void insertStudent(Scanner input) {
		boolean isError = false;

		System.out.print("Student name: ");
		String name = input.nextLine();
		if (name.length() > 20)
			name = name.substring(0, 20);
		System.out.print("CSAT score: ");
		int csat_score = input.nextInt();
		if ((csat_score < 0) || (csat_score > 400)) {
			System.out.println("CSAT score should be between 0 and 400.");
			return;
		}
		System.out.print("High school record score: ");
		int school_score = input.nextInt();
		if ((school_score < 0) || (school_score > 100)) {
			System.out
					.println("High school records score should be between 0 and 100.");
			return;
		}

		String sql1 = "SELECT * FROM student order by ID desc";
		String sql2 = "INSERT INTO student VALUES(?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement stmt1 = conn.prepareStatement(sql1);
			ResultSet rs1 = stmt1.executeQuery();
			int ID;
			if (rs1.next())
				ID = rs1.getInt("ID");
			else
				ID = 0;
			PreparedStatement stmt2 = conn.prepareStatement(sql2);
			stmt2.setInt(1, ID + 1);
			stmt2.setString(2, name);
			stmt2.setInt(3, csat_score);
			stmt2.setInt(4, school_score);
			stmt2.setBoolean(5, false);
			stmt2.setBoolean(6, false);
			stmt2.setBoolean(7, false);
			stmt2.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
		System.out.println("A student is successfully inserted");
	}

	public static void removeStudent(Scanner input) {
		
		System.out.print("Student id: ");
		int s_id = input.nextInt();
		String sql1 = "SELECT * FROM student WHERE ID = "+s_id+";";
		String sql2 = "DELETE FROM application WHERE s_id ="+s_id+";";
		String sql3 = "DELETE FROM student WHERE ID ="+s_id+";";
		PreparedStatement stmt1;
		PreparedStatement stmt2;
		PreparedStatement stmt3;
		ResultSet rs1;
		try {
			
			stmt1 = conn.prepareStatement(sql1);
			rs1 = stmt1.executeQuery();
			if(!rs1.next())
			{
				System.out.println("Student "+s_id+" doesn't exist.");
				return;
			}
			stmt2 = conn.prepareStatement(sql2);
			stmt2.executeUpdate();
			stmt3 = conn.prepareStatement(sql3);
			stmt3.executeUpdate();
			
			System.out.println("A student is successfully deleted.");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void makeApplication(Scanner input) {
		System.out.print("Student ID: ");
		int s_id = input.nextInt();
		System.out.print("University ID: ");
		int u_id = input.nextInt();

		try {
			String sql1 = "SELECT * FROM student WHERE ID=" + s_id;
			PreparedStatement stmt1 = conn.prepareStatement(sql1);
			ResultSet rs1 = stmt1.executeQuery();
			String sql2 = "SELECT * FROM university WHERE ID=" + u_id;
			PreparedStatement stmt2 = conn.prepareStatement(sql2);
			ResultSet rs2 = stmt2.executeQuery();

			if (!rs1.next())
				System.out.println("Student " + s_id + " doesn't exist.");
			else if (!rs2.next())
				System.out.println("University " + u_id + " doesn't exist.");
			else
				makeApplication_slave(rs1, rs2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void makeApplication_slave(ResultSet s, ResultSet u) {
		try {
			String u_group = u.getString("univ_group");
			if (s.getBoolean("apply" + u_group))
				System.out.println("A student can apply up to one university per group");
			else {
				
				// 1. application table에 레코드 삽입
				String sql1 = "INSERT INTO application VALUES(?, ?, ?)";
				PreparedStatement stmt1 = conn.prepareStatement(sql1);
				stmt1.setInt(1, u.getInt("ID"));
				stmt1.setInt(2, s.getInt("ID"));
				float score = s.getInt("csat_score")+u.getFloat("weight")*s.getInt("school_score");
				stmt1.setFloat(3, score);
				stmt1.executeUpdate();
				
				// 2. student table의 해당 record의 apply 를 false -> true로 업데이트
				String sql2 = "UPDATE student set apply"+u_group+" = true where ID ="+s.getInt("ID")+";";
				PreparedStatement stmt2 = conn.prepareStatement(sql2);
				stmt2.executeUpdate();
				
				System.out.println("Successfully made an application.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printAppliedStudent(Scanner input)
	{
		System.out.print("University ID: ");
		int u_id = input.nextInt();
		if(!checkUnivID(u_id))
		{
			System.out.println("University "+u_id+" doesn't exist.");
			return;
		}
		else
		{
			String sql = "SELECT * FROM student WHERE ID in ";
			sql += "(SELECT s_id FROM application WHERE u_id="+u_id+");";
			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				String result = "---------------------------------------------------------------------------\n";
				result += "id\tname\tcsat_score\tschool_score\n";
				result += "---------------------------------------------------------------------------\n";
				while (rs.next()) {
					int ID = rs.getInt("ID");
					String name = rs.getString("name");
					int csat_score = rs.getInt("csat_score");
					int school_score = rs.getInt("school_score");
					result += (ID + "\t" + name + "\t" + csat_score + "\t"
							+ school_score + "\n");
				}
				result += "---------------------------------------------------------------------------\n";
				System.out.print(result);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void printAppliedUniversity(Scanner input)
	{
		System.out.print("Student ID: ");
		int s_id = input.nextInt();
		if(!checkStudentID(s_id))
		{
			System.out.println("University "+s_id+" doesn't exist.");
			return;
		}
		else
		{
			String sql = "SELECT * FROM university WHERE ID in ";
			sql += "(SELECT u_id FROM application WHERE s_id="+s_id+");";
			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				String result = "---------------------------------------------------------------------------\n";
				result += "id\tname\tcapacity\tgroup\tweight\tapplied\n";
				result += "---------------------------------------------------------------------------\n";
				while (rs.next()) {
					int ID = rs.getInt("ID");
					String name = rs.getString("name");
					int capacity = rs.getInt("capacity");
					String group = rs.getString("univ_group");
					float weight = rs.getFloat("weight");
					int applied = getStudentNumber(ID);
					result += (ID + "\t" + name + "\t" + capacity + "\t" + group
							+ "\t" + Math.round(weight*100)/100.0 + "\t" + applied + "\n");
				}
				result += "---------------------------------------------------------------------------\n";
				System.out.print(result);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void printExpectedStudent(Scanner input)
	{
		System.out.print("University ID: ");
		int u_id = input.nextInt();
		if(!checkUnivID(u_id))
		{
			System.out.println("University "+u_id+" doesn't exist.");
			return;
		}
		else
		{
			String sql1 = "SELECT capacity FROM university WHERE ID="+u_id+";";
			String sql2 = "SELECT ID, name, school_score, csat_score, S.score ";
			sql2 += "FROM student, (SELECT s_id, score FROM application WHERE u_id="+u_id+") as S ";
			sql2 += "WHERE ID = S.s_id ";
			sql2 += "order by score desc, school_score desc, ID asc;";
			try {
				
				PreparedStatement stmt1 = conn.prepareStatement(sql1);
				PreparedStatement stmt2 = conn.prepareStatement(sql2);
				ResultSet rs1 = stmt1.executeQuery();
				ResultSet rs2 = stmt2.executeQuery();
				rs1.next();
				int capacity = rs1.getInt("capacity");
				
				if(getStudentNumber(u_id)<=capacity)
				{
					// print all
					String result = "---------------------------------------------------------------------------\n";
					result += "id\tname\tschool_score\tcsat_score\n";
					result += "---------------------------------------------------------------------------\n";
					while (rs2.next()) {
						int ID = rs2.getInt("ID");
						String name = rs2.getString("name");
						int school_score = rs2.getInt("school_score");
						int csat_score = rs2.getInt("csat_score");
						result += (ID + "\t" + name + "\t" + school_score + "\t"
								+ csat_score + "\n");
					}
					result += "---------------------------------------------------------------------------\n";
					System.out.print(result);
				}
				else
				{
					int upper = capacity;
					int lower = capacity;
					int allowNum = capacity + (capacity+9)/10;
					
					rs2.absolute(capacity);
					float score = rs2.getFloat("S.score");
					int school_score = rs2.getInt("school_score");
					
					while(rs2.next())
					{
						if(!((rs2.getFloat("S.score")==score)&&(rs2.getInt("school_score")==school_score))) break;
						upper++;
					}
					rs2.absolute(capacity);
					while(rs2.previous())
					{
						if(!((rs2.getFloat("S.score")==score)&&(rs2.getInt("school_score")==school_score))) break;
						lower--;
					}
					
					int maxNum;
					int num = 1;
					if(upper<=allowNum) maxNum = upper;
					else maxNum = lower-1;
					rs2.absolute(0);
					String result = "---------------------------------------------------------------------------\n";
					result += "id\tname\tschool_score\tcsat_score\n";
					result += "---------------------------------------------------------------------------\n";
					while(rs2.next() && num<=maxNum)
					{
						int ID = rs2.getInt("ID");
						String name = rs2.getString("name");
						school_score = rs2.getInt("school_score");
						int csat_score = rs2.getInt("csat_score");
						result += (ID + "\t" + name + "\t" + school_score + "\t"
								+ csat_score + "\n");
						num++;
					}
					result += "---------------------------------------------------------------------------\n";
					System.out.print(result);
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}		
	}
	public static void printExpectedUniversity(Scanner input)
	{
		System.out.print("Student ID: ");
		int s_id = input.nextInt();
		if(!checkStudentID(s_id))
		{
			System.out.print("Student "+s_id+" doesn't exist.");
			return;
		}
		else
		{
			String sql = "SELECT u_id FROM application WHERE s_id="+s_id+";";
			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				String result = "---------------------------------------------------------------------------\n";
				result += "id\tname\tcapacity\tgroup\tweight\tapplied\n";
				result += "---------------------------------------------------------------------------\n";				
				while(rs.next())
				{
					result+=checkAdmission(s_id, rs.getInt("u_id"));
				}
				result += "---------------------------------------------------------------------------\n";
				System.out.print(result);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean checkUnivID(int u_id)
	{
		String sql = "SELECT * FROM university WHERE ID = "+u_id+";";
		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if(rs.next()) result = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static boolean checkStudentID(int s_id)
	{
		String sql = "SELECT * FROM student WHERE ID = "+s_id+";";
		PreparedStatement stmt;
		ResultSet rs;
		boolean result = false;
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if(rs.next()) result = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static int getStudentNumber(int u_id)
	{
		String sql = "SELECT count(*) FROM application WHERE u_id="+u_id+";";
		int result = 0;
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			result = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static String checkAdmission(int s_id, int u_id)
	{
		boolean admission = false;
		if(getStudentNumber(u_id)<=getCapacity(u_id)) admission = true;
		else
		{
			String sql = "SELECT ID, name, school_score, csat_score, S.score ";
			sql += "FROM student, (SELECT s_id, score FROM application WHERE u_id="+u_id+") as S ";
			sql += "WHERE ID = S.s_id ";
			sql += "order by score desc, school_score desc;";
			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				while(rs.next())
				{
					if(rs.getInt("ID")==s_id) break;
				}
				float score = rs.getFloat("S.score");
				int school_score = rs.getInt("school_score");
				int rank = rs.getRow();
				while(rs.next())
				{
					if(!(rs.getFloat("S.score")==score)&&(rs.getInt("school_score")==school_score)) break;
					rank++;
				}
				
				int capacity = getCapacity(u_id);
				int allowNum = capacity + (capacity+9)/10;
				if(rank<=allowNum) admission = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		String result = "";
		if(admission)
		{
			String sql = "SELECT * FROM university WHERE ID="+u_id+";";
			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				rs.next();
				result += (rs.getInt(1)+"\t"+rs.getString(2)+"\t"+rs.getInt(3)+"\t"+rs.getString(4)+"\t");
				result += (Math.round(rs.getFloat(5)*100)/100.0+"\t"+getStudentNumber(u_id)+"\n");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	public static int getCapacity(int u_id)
	{
		String sql = "SELECT capacity FROM university WHERE ID="+u_id+";";
		int result = -1;
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			result = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
