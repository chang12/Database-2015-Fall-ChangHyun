# 프로젝트 소개

서울대학교 컴퓨터공학부 **데이타베이스**(2015-가을) 강좌의 **첫번째 프로젝트 과제**입니다. 간단한 SQL을 처리하는 RDBMS를 만들어보는 것이 목표였습니다. DML까지 구현하는 것이 목표였지만, DDL까지 구현했습니다.

```sql
create table table_name
(
	column_name data_type [not null],
		...
			primary key(column_name1, column_name2, ...),
				[foreign key(column_name3) references table_name1(column_name4),]
					[foreign key(column_name5) references table_name2(column_name6)]
						...
						)
						```
						```sql
						drop table table_name;
						```
						```sql
						desc table_name1, table_name2, ..., table_nameK;
						```

						자세한 내용은 [프로젝트 소개 문서](http://ids.snu.ac.kr/w/images/c/c0/DB2015FPRJ1_2_updated.pdf)를 참고해주세요.


						# 개발환경

						* **Eclipse** (Java)
						* **JavaCC** Eclipse Plug-in
						* **Berkeley DB** Java API

						# 실행방법

						1. **simple-rdbms.jar** 파일 다운로드
						2. **db** 디렉토리 생성
						3. `java -jar simple-rdbms.jar`
