if (entries!=null) {
            try{
                String framework = "embedded";
                String protocol = "jdbc:derby:";
                String dbName = "derbyDB";
                ArrayList<Statement> statements = new ArrayList<Statement>();
                PreparedStatement psInsert;
                PreparedStatement psUpdate;
                Statement s;
                ResultSet rs = null;

                // View classpath
                String classpathStr = System.getProperty("java.class.path");
                System.out.println(classpathStr);

                Properties props = new Properties(); // connection properties
                props.put("user", "user1");
                props.put("password", "user1");

                Connection conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
                System.out.println("Connected to and created database " + dbName);
                conn.setAutoCommit(false);

                s = conn.createStatement();
                statements.add(s);

                // We create a table...
                s.execute("create table references(idRef int, author varchar(50), " +
                        "doi varchar(50), year int, citeKey, booktitle, title,  journal, keywords, " +
        "number, numpages, pages, volume,dl varchar(150), abstract varchar(1000), PRIMARY KEY (dl))");
        s.execute("ALTER TABLE table_name ADD CONSTRAINT digitalLibraries constraint (dl)");
                /*digitalLibraries(dl,name,url); primaryKey(dl) */
                System.out.println("Created table digitalLibraries");

                // and add a few rows...

                // parameter 1 is num (int), parameter 2 is addr (varchar)
                psInsert = conn.prepareStatement(
                        "insert into digitalLibraries values (?, ?, ?)");

                statements.add(psInsert);
                //ex int : psInsert.setInt(1, 1900);

                psInsert.setString(1, "IEEExplore");
                psInsert.setString(2, "IEE Explore");
                psInsert.setString(3, "https://ieeexplore.ieee.org/Xplore/home.jsp");
                psInsert.executeUpdate();
                System.out.println("Inserted ('IEEE', 'IEEE Xplore', 'https://ieeexplore.ieee.org/Xplore/home.jsp') ");

                psInsert.setString(1, "ACM");
                psInsert.setString(2, "ACM DL");
                psInsert.setString(3, "https://dl.acm.org/");
                psInsert.executeUpdate();
                System.out.println("Inserted ('ACM', 'ACM DL', 'https://dl.acm.org/') ");

                psInsert.setString(1, "ScienceDirect");
                psInsert.setString(2, "ScienceDirect");
                psInsert.setString(3, "https://www.sciencedirect.com/");
                psInsert.executeUpdate();
                System.out.println("Inserted ('ScienceDirect', 'ScienceDirect', 'https://www.sciencedirect.com/')' ");

                psInsert.setString(1, "SpringerLink");
                psInsert.setString(2, "SpringerLink");
                psInsert.setString(3, "https://link.springer.com/");
                psInsert.executeUpdate();
                System.out.println("Inserted ('SpringerLink', 'SpringerLink', 'https://link.springer.com/') ");

                psInsert.setString(1, "Scopus");
                psInsert.setString(2, "Scopus");
                psInsert.setString(3, "https://www.scopus.com/");
                psInsert.executeUpdate();
                System.out.println("Inserted ('Scopus', 'Scopus', 'https://www.scopus.com/') ");

                psInsert.setString(1, "WebOfScience");
                psInsert.setString(2, "Web of Science");
                psInsert.setString(3, "https://mjl.clarivate.com/home");
                psInsert.executeUpdate();
                System.out.println("Inserted ('WebOfScience', 'Web of Science', 'https://mjl.clarivate.com/home') ");

                // Select data
                rs = s.executeQuery("SELECT * FROM digitalLibraries");

            while (rs.next()){
                    System.out.println(rs.getString(1));
                }
                // delete the table BORRAR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                s.execute("drop table digitalLibraries");
                System.out.println("Dropped table digitalLibraries");

                conn.commit();
                System.out.println("Committed the transaction");

            } catch (SQLException e){
                System.out.println("Error");
                while (e != null) {
                    System.err.println("\n----- SQLException -----");
                    System.err.println("  SQL State:  " + e.getSQLState());
                    System.err.println("  Error Code: " + e.getErrorCode());
                    System.err.println("  Message:    " + e.getMessage());
                    // for stack traces, refer to derby.log or uncomment this:
                    //e.printStackTrace(System.err);
                    e = e.getNextException();
                }
            }
        }