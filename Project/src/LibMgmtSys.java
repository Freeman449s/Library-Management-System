/* 
 * Copyright 2020 Minzhe Tang
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.sql.*;
import java.util.*;
import java.io.*;

public class LibMgmtSys {
    public static void main(String args[]) {
        Connection conn = DBUtil.connect();
        Map<String, Admin> admins = new HashMap<String, Admin>();
        welcome(conn, admins);
    }

    //非基本类型或String, 传入引用
    protected static void welcome(Connection conn, Map<String, Admin> admins) {
        while (true) {
            System.out.println("©2020, Gordon Freeman, Library Management System, Ver1.0.0 Alpha");
            System.out.println("Welcome to the library management system.");
            System.out.println("What would you like to do?");
            System.out.println("(Please input the number of your choice.)");
            System.out.println("1. Start An Inquiry");
            System.out.println("2. Administrator Login");
            System.out.println("0. Exit");
            boolean valid = false;
            int choice = 0;
            Scanner in = new Scanner(System.in);
            while (!valid) {
                choice = in.nextInt();
                if (choice >= 1 && choice <= 2 || choice == 0) valid = true;
                else System.out.println("Invalid input, please try again.");
            }
            if (choice == 1) inquiry(conn);
            else if (choice == 2) login(conn, admins);
            else if (choice == 0) {
                exit(conn);
                return;
            }
        }
    }

    protected static void inquiry(Connection conn) {
        while (true) {
            System.out.println("Please specify the key(s) by which you wish to query books.");
            System.out.println("You can use multiple keys for query. Use [9] to end your input.");
            System.out.println("Note: records will be ordered by the first key you input.");
            System.out.println("1. By Title");
            System.out.println("2. By Type");
            System.out.println("3. By Press");
            System.out.println("4. By Publication Year");
            System.out.println("5. By Author");
            System.out.println("6. By Price");
            System.out.println("0. Return");
            Scanner in = new Scanner(System.in);
            PreparedStatement pstmt = null;
            int choice;
            int intOrderKey;
            boolean firstCondition = true;
            String sql = "select * from book where ";
            intOrderKey = choice = in.nextInt();
            while (true) {
                if (choice == 0) return;
                else if (choice == 1) {
                    System.out.println("Please input the title:");
                    //新建Scanner对象实现清空输入流
                    in = new Scanner(System.in);
                    String title = in.nextLine();
                    if (!firstCondition) sql = sql.concat(" and ");
                    else firstCondition = false;
                    sql = sql.concat("title = '");
                    sql = sql.concat(title);
                    sql = sql.concat("'");
                }
                else if (choice == 2) {
                    System.out.println("Please input the type:");
                    in = new Scanner(System.in);
                    String type = in.nextLine();
                    if (!firstCondition) sql = sql.concat(" and ");
                    else firstCondition = false;
                    sql = sql.concat("type = '");
                    sql = sql.concat(type);
                    sql = sql.concat("'");
                }
                else if (choice == 3) {
                    System.out.println("Please input the name of the press:");
                    in = new Scanner(System.in);
                    String press = in.nextLine();
                    if (!firstCondition) sql = sql.concat(" and ");
                    else firstCondition = false;
                    sql = sql.concat("press = '");
                    sql = sql.concat(press);
                    sql = sql.concat("'");
                }
                else if (choice == 4) {
                    System.out.println("Please input a section of publication year. Use [Enter] to devide.");
                    String start = in.next();
                    String end = in.next();
                    if (!firstCondition) sql = sql.concat(" and ");
                    else firstCondition = false;
                    sql = sql.concat("pub_year >= ");
                    sql = sql.concat(start);
                    sql = sql.concat(" and ");
                    sql = sql.concat("pub_year <= ");
                    sql = sql.concat(end);
                }
                else if (choice == 5) {
                    System.out.println("Please input the name of the author:");
                    in = new Scanner(System.in);
                    String author = in.nextLine();
                    if (!firstCondition) sql = sql.concat(" and ");
                    else firstCondition = false;
                    sql = sql.concat("author = '");
                    sql = sql.concat(author);
                    sql = sql.concat("'");
                }
                else if (choice == 6) {
                    System.out.println("Please input a section of price. Use [Enter] to devide.");
                    String lower = in.next();
                    String upper = in.next();
                    if (!firstCondition) sql = sql.concat(" and ");
                    else firstCondition = false;
                    sql = sql.concat("price >= ");
                    sql = sql.concat(lower);
                    sql = sql.concat(" and ");
                    sql = sql.concat("price <= ");
                    sql = sql.concat(upper);
                }
                else if (choice == 9) {
                    sql = sql.concat(" order by ");
                    String orderKey = "title";
                    if (intOrderKey == 1) orderKey = "title";
                    else if (intOrderKey == 2) orderKey = "type";
                    else if (intOrderKey == 3) orderKey = "press";
                    else if (intOrderKey == 4) orderKey = "pub_year";
                    else if (intOrderKey == 5) orderKey = "author";
                    else if (intOrderKey == 6) orderKey = "price";
                    sql = sql.concat(orderKey);
                    sql = sql.concat(";");
                    break;
                }
                choice = in.nextInt();
            }
            try {
                pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                boolean existsItem = false;
                if (rs.next()) {
                    existsItem = true;
                    do {
                        String bno = rs.getString("bno");
                        String type = rs.getString("type");
                        String title = rs.getString("title");
                        String press = rs.getString("press");
                        int pub_year = rs.getInt("pub_year");
                        String author = rs.getString("author");
                        double price = rs.getDouble("price");
                        int total = rs.getInt("total");
                        int stock = rs.getInt("stock");
                        System.out.println(bno + "    " + type + "    " + title + "    "
                                + press + "    " + pub_year + "    " + author + "    "
                                + price + "    " + total + "    " + stock);
                    } while (rs.next());
                }
                else System.out.println("No Available Items Found");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void login(Connection conn, Map<String, Admin> admins) {
        System.out.println("Please input your account and password. Use [Enter] to devide.");
        String query = "select * from admin";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        if (admins.isEmpty()) {
            try {
                //有准备地执行SQL语句，允许使用占位符接收参数，且具有预编译机制，相比直接执行更快
                pstmt = conn.prepareStatement(query);
                //存放从表中获取的元组
                rs = (ResultSet) pstmt.executeQuery();
                //next方法将光标下移一行。第一次调用时使得光标位于第一行
                while (rs.next()) {
                    String id = rs.getString("id");
                    String pwd = rs.getString("pwd");
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    admins.put(id, new Admin(id, pwd, name, phone));
                }
                DBUtil.close(rs);
                DBUtil.close(pstmt);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Scanner in = new Scanner(System.in);
        boolean valid = false;
        while (!valid) {
            String id = in.nextLine();
            String pwd = in.nextLine();
            if (admins.containsKey(id) && admins.get(id).judgePwd(pwd)) {
                System.out.println("Login success!");
                valid = true;
                manaPanel(conn, admins.get(id));
            }
            else {
                System.out.println("Invalid account or false password, please try again!");
            }
        }
        return;
    }

    protected static void manaPanel(Connection conn, Admin curAdmin) {
        System.out.println("Welcome, " + curAdmin.name);
        while (true) {
            System.out.println("What would you like to do?");
            System.out.println("1. Start An Inquiry");
            System.out.println("2. Warehouse New Books");
            System.out.println("3. Create A New Lending Record");
            System.out.println("4. Conduct A Book Return");
            System.out.println("5. Manage Library Cards");
            System.out.println("0. Logout");
            Scanner in = new Scanner(System.in);
            int choice = in.nextInt();
            if (choice == 0) return;
            else if (choice == 1) inquiry(conn);
            else if (choice == 2) warehouse(conn);
            else if (choice == 3) lend(conn, curAdmin);
            else if (choice == 4) Return(conn);
            else if (choice == 5) cardMana(conn);
        }
    }

    //warehouse: 入库
    protected static void warehouse(Connection conn) {
        while (true) {
            System.out.println("In which way do you wish to enter book information?");
            System.out.println("1. Enter by Hand");
            System.out.println("2. Enter by File");
            System.out.println("0. Cancel");
            Scanner in = new Scanner(System.in);
            int choice = in.nextInt();
            if (choice == 0) return;
            else if (choice == 1) {
                System.out.println("Please enter the book information in the following order: " +
                        "bno, type, title, press, pub_year, author, price, total, stock.");
                System.out.println("Note: Use ',' to devide, use '#' to end you input.");
                in = new Scanner(System.in);
                String input = in.nextLine();
                //subString方法获取的字串不包括位置在endIndex的字符
                while (!input.substring(0, 1).equals("#")) {
                    newBookInfo(conn, input);
                    input = in.nextLine();
                }
            }
            else if (choice == 2) {
                System.out.println("To use batch import function, please input the complete .txt file path (postfix included).");
                System.out.println("Note: in the file, please input the information of a book " +
                        "(ie. bno, type, title, press, pub_year, author, price, total, stock) in one line, use ',' to devide.");
                System.out.println("Note: '#' is not required for batch import.");
                in = new Scanner(System.in);
                String path = in.nextLine();
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(path));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    String info;
                    //readLine方法在读到文件尾时返回null
                    while ((info = br.readLine()) != null) {
                        newBookInfo(conn, info);
                    }
                    System.out.println("Operation Completed Successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected static void lend(Connection conn, Admin curAdmin) {
        String cno = readCno(conn);
        Scanner in = new Scanner(System.in);
        String sql = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        while (true) {
            System.out.println("What would you like to do?");
            System.out.println("1. Inquire about Borrowed Books");
            System.out.println("2. Create A New Lending Record");
            System.out.println("0. Return");
            int choice;
            choice = in.nextInt();
            if (choice == 0) {
                DBUtil.close(rs);
                DBUtil.close(pstmt);
                return;
            }
            else if (choice == 1) borrowedBooks(conn, cno);
            else if (choice == 2) {
                boolean valid = false;
                String bno = null;
                while (!valid) {
                    System.out.println("Please input the book number:");
                    in = new Scanner(System.in);
                    bno = in.nextLine();
                    valid = checkBook(conn, bno);
                }
                try {
                    sql = "select * from borrow where bno = ? and cno = ? and return_date is null";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, bno);
                    pstmt.setString(2, cno);
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        System.out.println("This member has already borrowed this book, and has not returned it yet.");
                        System.out.println("Operation Terminated");
                        continue;
                    }
                    sql = "select stock from book where bno = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, bno);
                    rs = pstmt.executeQuery();
                    rs.next();
                    int stock = rs.getInt("stock");
                    if (stock > 0) {
                        sql = "insert into borrow values(?,?,?,?,?)";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, cno);
                        pstmt.setString(2, bno);
                        //java.util.Date类的默认构造依据当前时间构造对象
                        java.sql.Date curDate = new java.sql.Date(new java.util.Date().getTime());
                        pstmt.setDate(3, curDate);
                        pstmt.setNull(4, Types.DATE);
                        pstmt.setString(5, curAdmin.id);
                        pstmt.execute();
                        stock--;
                        sql = "update book set stock = ? where bno = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, stock);
                        pstmt.setString(2, bno);
                        pstmt.execute();
                        System.out.println("Operation Completed Successfully");
                    }
                    else {
                        System.out.println("No such book left. Operation terminated.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected static void Return(Connection conn) {
        String cno = readCno(conn);
        Scanner in = new Scanner(System.in);
        String sql = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        while (true) {
            System.out.println("What would you like to do?");
            System.out.println("1. Inquire about Borrowed Books");
            System.out.println("2. Return A Book");
            System.out.println("0. Return");
            int choice = in.nextInt();
            if (choice == 0) {
                DBUtil.close(rs);
                DBUtil.close(pstmt);
                return;
            }
            else if (choice == 1) borrowedBooks(conn, cno);
            else if (choice == 2) {
                boolean valid = false;
                String bno = null;
                System.out.println("Please input the book number:");
                while (true) {
                    in = new Scanner(System.in);
                    bno = in.nextLine();
                    valid = checkBook(conn, bno);
                    if (valid) break;
                    System.out.println("No such book in the library, please try again!");
                }
                try {
                    //检查该本书是否未还
                    sql = "select * from borrow where bno = ? and cno = ? and return_date is null";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, bno);
                    pstmt.setString(2, cno);
                    rs = pstmt.executeQuery();
                    if (!rs.next()) {
                        System.out.println("This member hasn't borrowed this book, or he/she has returned it.");
                        continue;
                    }
                    //更新借阅记录
                    sql = "update borrow set return_date = ? where bno = ? and cno = ? and return_date is null";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(2, bno);
                    pstmt.setString(3, cno);
                    java.sql.Date curDate = new java.sql.Date(new java.util.Date().getTime());
                    pstmt.setDate(1, curDate);
                    pstmt.execute();
                    //获取当前存量
                    sql = "select stock from book where bno = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, bno);
                    rs = pstmt.executeQuery();
                    rs.next();
                    int stock = rs.getInt("stock");
                    stock++;
                    //更新存量
                    sql = "update book set stock = ? where bno = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(2, bno);
                    pstmt.setInt(1, stock);
                    pstmt.execute();
                    System.out.println("Operation Completed Successfully");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected static void cardMana(Connection conn) {
        while (true) {
            System.out.println("What would you like to do?");
            System.out.println("1. Register A New Card");
            System.out.println("2. Cancel A Card");
            System.out.println("0. Return");
            Scanner in = new Scanner(System.in);
            String sql = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            int choice = in.nextInt();
            if (choice == 0) return;
            else if (choice == 1) {
                System.out.println("Please input the card number for the new member:");
                in = new Scanner(System.in);
                String cno = in.nextLine();
                try {
                    sql = "select * from card where cno = ?";
                    pstmt = conn.prepareStatement(sql);
                    while (true) {
                        pstmt.setString(1, cno);
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            System.out.println("There's already a card of this number, please try another.");
                            in = new Scanner(System.in);
                            cno = in.nextLine();
                        }
                        else break;
                    }
                    sql = "insert into card values (?, ?, ?, ?)";
                    System.out.println("Please input the name, department of the member, and the type ('t', 's' or 'o') of the card:");
                    System.out.println("Note: please use ',' to devide.");
                    in = new Scanner(System.in);
                    String input = in.nextLine();
                    String strArr[] = input.split(",");
                    String name = strArr[0];
                    String dept = strArr[1];
                    String type = strArr[2];
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, cno);
                    pstmt.setString(2, name);
                    pstmt.setString(3, dept);
                    pstmt.setString(4, type);
                    pstmt.execute();
                    System.out.println("Operation Completed Successfully");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if (choice == 2) {
                String cno = readCno(conn);
                try {
                    sql = "select * from borrow where cno = ? and return_date is null";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, cno);
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        System.out.println("This member has book(s) not returned. Cannot cancel this card.");
                        continue;
                    }
                    else {
                        System.out.println("The information of this card, together with all the lending records " +
                                "will be removed from the database, are you sure you want to do this?");
                        System.out.println("[Y]/[N]");
                        String choice2 = in.next();
                        if (choice2.equals("N")) {
                            System.out.println("Command has not been executed.");
                            continue;
                        }
                        else if (choice2.equals("Y")) {
                            sql = "delete from borrow where cno = ?";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setString(1, cno);
                            pstmt.execute();
                            sql = "delete from card where cno = ?";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setString(1, cno);
                            pstmt.execute();
                            System.out.println("Operation Completed Successfully");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected static void exit(Connection conn) {
        System.out.println("Thanks for using the library management system!");
        DBUtil.close(conn);
    }

    //将传入的新书信息加入数据库，或更新已有书籍的stock与total
    protected static void newBookInfo(Connection conn, String info) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;
        String arr[] = info.split(",");
        String bno = arr[0];
        String type = arr[1];
        String title = arr[2];
        String press = arr[3];
        int pub_year = Integer.parseInt(arr[4]);
        String author = arr[5];
        double price = Double.parseDouble(arr[6]);
        int total = Integer.parseInt(arr[7]);
        int stock = Integer.parseInt(arr[8]);
        sql = "select * from book where bno = ?;";
        boolean exists = false;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bno);
            rs = pstmt.executeQuery();
            if (rs.next()) exists = true;
            else exists = false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (exists) {
            //使用setString方法，字串外无需再加单引号
            sql = "update book set stock = ?, total = ? where bno = ?;";
            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(3, bno);
                pstmt.setInt(1, stock);
                pstmt.setInt(2, total);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            sql = "insert into book values(?,?,?,?,?,?,?,?,?);";
            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, bno);
                pstmt.setNString(2, type);
                pstmt.setNString(3, title);
                pstmt.setNString(4, press);
                pstmt.setInt(5, pub_year);
                pstmt.setNString(6, author);
                pstmt.setDouble(7, price);
                pstmt.setInt(8, total);
                pstmt.setInt(9, stock);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        DBUtil.close(rs);
        DBUtil.close(pstmt);
    }

    //输出已借且未归还的书籍
    protected static void borrowedBooks(Connection conn, String cno) {
        String sql = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        sql = "select * from borrow where cno = ? and return_date is null";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cno);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                do {
                    String bno = rs.getString("bno");
                    String borrowDate = rs.getDate("borrow_date").toString();
                    String handler = rs.getString("handler");
                    System.out.println(bno + "    " + borrowDate + "    " + handler);
                } while (rs.next());
            }
            else System.out.println("This member doesn't have any book not returned.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBUtil.close(rs);
        DBUtil.close(pstmt);
    }

    //函数要求用户输入一个已经存在的卡号，并返回此卡号
    protected static String readCno(Connection conn) {
        System.out.println("Please input the card number:");
        Scanner in = new Scanner(System.in);
        String cno = in.nextLine();
        String sql = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean valid = false;
        while (!valid) {
            try {
                sql = "select * from card where cno = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, cno);
                rs = pstmt.executeQuery();
                if (!rs.next()) {
                    System.out.println("No such card, please try again!");
                    in = new Scanner(System.in);
                    cno = in.nextLine();
                }
                else valid = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cno;
    }

    //检查一本书是否已在库中
    protected static boolean checkBook(Connection conn, String bno) {
        String sql = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            sql = "select * from book where bno = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bno);
            rs = pstmt.executeQuery();
            if (rs.next()) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBUtil.close(rs);
        DBUtil.close(pstmt);
        return false;
    }

}

class DBUtil {
    public static Connection connect() {
        //驱动类名
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        //数据库地址
        String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=EXP5";
        String userName = "sa";
        String userPwd = "123456";
        Connection conn = null;
        try {
            //加载驱动
            Class.forName(driverName);
            conn = DriverManager.getConnection(dbURL, userName, userPwd);
        }
        //捕捉Exception类：所有异常类的父类
        catch (Exception e) {
            //打印出错位置和原因
            e.printStackTrace();
        }
        return conn;
    }

    public static void close(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

class Admin {
    protected String id;
    protected String pwd;
    protected String name;
    protected String phone;

    Admin(String idInput, String pwdInput, String nameInput, String phoneInput) {
        id = idInput;
        pwd = pwdInput;
        name = nameInput;
        phone = phoneInput;
    }

    protected boolean judgePwd(String input) {
        //"=="比较引用，equals比较值
        if (pwd.equals(input)) return true;
        else return false;
    }
}

class Book {
    protected String bno;
    protected String type;
    protected String title;
    protected String press;
    protected int pub_year;
    protected String author;
    protected double price;
    protected int total;
    protected int stock;

    Book(String bno, String type, String title, int pub_year, String author, double price, int total, int stock) {
        this.bno = bno;
        this.type = type;
        this.title = title;
        this.press = press;
        this.pub_year = pub_year;
        this.author = author;
        this.price = price;
        this.total = total;
        this.stock = stock;
    }
}