package hashbrowns.p1.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hashbrowns.p1.utils.*;
import hashbrowns.p1.annotations.Id;
import hashbrowns.p1.exceptions.RecipeNameAlreadyExists;
import hashbrowns.p1.exceptions.UsernameAlreadyExistsException;
import hashbrowns.p1.utils.Connect;

public class ORMImpl implements ORM{

	private Connect con = Connect.getConnect();
	Logger logger = Logger.getLogger();


	// Might have to return object for future use
	public <T> Object insertObject(Object object) throws UsernameAlreadyExistsException, RecipeNameAlreadyExists{
		Object obj = null;
		try (Connection connection = con.getConnection()) {
			logger.log("ORM Attemps insertion", LoggingLevel.TRACE);
			T temp = (T) object.getClass().getConstructor().newInstance();
			PreparedStatement ps;
			int rowsAffected;
			connection.setAutoCommit(false);
			StringBuilder info = new StringBuilder();
			Class<?> clazz = object.getClass();
			Field[] fields = clazz.getDeclaredFields();
			info.append("insert into " + clazz.getSimpleName().toLowerCase() + " values (");
			for (Field field : fields) {
				field.setAccessible(true);
				Annotation annId = field.getAnnotation(Id.class);
				annId = field.getAnnotation(Id.class);
				if (annId != null) {
					info.append("default, ");
					field.set(temp, field.get(object));
				} else {
					info.append("'" + field.get(object) + "', ");
					field.set(temp, field.get(object));
				}

			}
			info.delete(info.length() - 2, info.length());
			info.append(");");
			ps = connection.prepareStatement(info.toString());
			rowsAffected = ps.executeUpdate();

			if (rowsAffected == 1) {
				logger.log("Insertion Completed", LoggingLevel.TRACE);
				connection.commit();
				obj = temp;
			} else {
				logger.log("Insertion Went Wrong", LoggingLevel.WARN);
				connection.rollback();
			}
		} catch (SQLException | IllegalArgumentException | IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new UsernameAlreadyExistsException();
		}
		return obj;
	}

	public <T> Object deleteObject(Object object) {
		Object obj = null;
		try (Connection connection = con.getConnection();) {
			logger.log("ORM Attemps Deletion", LoggingLevel.TRACE);
			T temp = (T) object.getClass().getConstructor().newInstance();
			PreparedStatement ps;
			int rowsAffected;
			connection.setAutoCommit(false);
			StringBuilder info = new StringBuilder();
			Class<?> clazz = object.getClass();
			Field[] fields = clazz.getDeclaredFields();
			info.append("delete from " + clazz.getSimpleName().toLowerCase() + " where id =");
			for (Field field : fields) {
				field.setAccessible(true);
				Annotation annId = field.getAnnotation(Id.class);
				annId = field.getAnnotation(Id.class);
				if (annId != null) {
					info.append(" " + field.get(object) + ";");
					field.set(temp, field.get(object));
				} else {
					field.set(temp, field.get(object));
				}
			}
			ps = connection.prepareStatement(info.toString());
			rowsAffected = ps.executeUpdate();

			if (rowsAffected == 1) {
				logger.log("Deletion Completed", LoggingLevel.TRACE);
				connection.commit();
				obj = temp;
			} else {
				logger.log("Deletion Went Wrong", LoggingLevel.WARN);
				connection.rollback();
			}

		} catch (SQLException | IllegalArgumentException | IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	public Object findById(Object object) {
		try (Connection connection = con.getConnection();) {
			logger.log("ORM Attemps findByID", LoggingLevel.TRACE);
			PreparedStatement ps;
			ResultSet rs;
			connection.setAutoCommit(false);
			StringBuilder info = new StringBuilder();
			Class<?> clazz = object.getClass();
			Field[] fields = clazz.getDeclaredFields();
			info.append("select * from " + clazz.getSimpleName().toLowerCase() + " where id=");

			for (Field field : fields) {
				field.setAccessible(true);
				Annotation annId = field.getAnnotation(Id.class);
				annId = field.getAnnotation(Id.class);
				if (annId != null) {
					info.append(" " + field.get(object) + ";");
				}
			}
			ps = connection.prepareStatement(info.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				logger.log("ID + Object Was Found", LoggingLevel.TRACE);
				for (Field field : clazz.getDeclaredFields()) {
					field.setAccessible(true);
					Annotation annId = field.getAnnotation(Id.class);
					annId = field.getAnnotation(Id.class);
					if (annId == null) {
						field.set(object, rs.getObject(field.getName().toString()));
					}
				}

			} else {
				logger.log("Invalid ID was inserted", LoggingLevel.INFO);
				object = null;
			}
		} catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;

	}

	public <T> Object updateObject(Object object) {
		Object obj = null;
		try (Connection connection = con.getConnection()) {
			logger.log("ORM Attemps to update", LoggingLevel.TRACE);
			T temp = (T) object.getClass().getConstructor().newInstance();
			PreparedStatement ps;
			int rowsAffected;
			connection.setAutoCommit(false);
			StringBuilder info = new StringBuilder();
			Class<?> clazz = object.getClass();
			Field[] fields = clazz.getDeclaredFields();
			info.append("update " + clazz.getSimpleName().toLowerCase() + " set");
			for (Field field : fields) {
				field.setAccessible(true);
				Annotation annId = field.getAnnotation(Id.class);
				annId = field.getAnnotation(Id.class);
				if (annId == null) {
					info.append(" " + field.getName() + " = " + "'" + field.get(object) + "'" + " ,");
					field.set(temp, field.get(object));
				}
			}
			info.delete(info.length() - 2, info.length());
			for (Field field : fields) {
				field.setAccessible(true);
				Annotation annId = field.getAnnotation(Id.class);
				annId = field.getAnnotation(Id.class);
				if (annId != null) {
					info.append(" where " + field.getName() + " = " + field.get(object) + ";");
					field.set(temp, field.get(object));
				}
			}

			ps = connection.prepareStatement(info.toString());
			rowsAffected = ps.executeUpdate();

			if (rowsAffected == 1) {
				logger.log("Update Completed", LoggingLevel.TRACE);
				obj = temp;
				connection.commit();
			} else {
				logger.log("Update Went Wrong", LoggingLevel.WARN);
				connection.rollback();
			}
		} catch (SQLException | IllegalArgumentException | IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	public <T> List<T> getAll(Object object) {
		List<T> all = new ArrayList<>();
		try (Connection connection = con.getConnection()) {
			logger.log("ORM request All", LoggingLevel.TRACE);
			PreparedStatement ps;
			ResultSet rs;
			StringBuilder info = new StringBuilder();
			Class<?> clazz = object.getClass();
			Field[] fields = clazz.getDeclaredFields();
			info.append("select * from " + clazz.getSimpleName().toLowerCase() + ";");
			ps = connection.prepareStatement(info.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				T temp = (T) object.getClass().getConstructor().newInstance();
				for (Field field : fields) {
					field.setAccessible(true);
					Annotation annId = field.getAnnotation(Id.class);
					annId = field.getAnnotation(Id.class);
					if (annId == null) {
						int column = rs.findColumn(field.getName());
						field.set(temp, rs.getObject(column));
					} else {
						int column = rs.findColumn(field.getName());
						field.set(temp, rs.getObject(column));
					}
				}
				all.add(temp);

			}

		} catch (SQLException | IllegalArgumentException | IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return all;
	}

}
