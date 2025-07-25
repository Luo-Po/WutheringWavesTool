package cn.tealc.wutheringwavestool.dao;

import com.kuro.kujiequ.model.roleData.Role;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName
 * @Description TODO
 * @Author Nefeli
 * @Data 2025/7/25 12:53
 * @Version 1.0
 */
public class PlanRoleDao {
    private static final Logger LOG = LoggerFactory.getLogger(PlanRoleDao.class);
    private final Connection con = JdbcUtils.getConnection();
    QueryRunner qr = new QueryRunner();

    private RowProcessor getRowProcessor() {
        Map<String, String> map = new HashMap<>();
        map.put("id", "id");
        map.put("name", "name");
        map.put("icon_url", "iconUrl");
        BeanProcessor beanProcessor = new BeanProcessor(map);
        return new BasicRowProcessor(beanProcessor);
    }

    public Integer addOrUpdatePlanRole(String id, String name, String iconUrl) {
        String sql = """
                INSERT INTO plan_role (id,name,icon_url)
                VALUES (?,?,?)
                ON CONFLICT (id) DO UPDATE SET
                    name=?,
                    icon_url=?
                """;
        try {
            ResultSetHandler<Integer> rsh = new ScalarHandler<Integer>();
            return qr.insert(con, sql, rsh,
                    id, name, iconUrl,
                    name, iconUrl);
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public String getNameById(String id) {
        String sql = "SELECT DISTINCT name FROM plan_role WHERE id=?";
        try {
            ResultSetHandler<String> rsh = new ScalarHandler<String>();
            return qr.query(con, sql, rsh, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getIconById(String id) {
        String sql = "SELECT DISTINCT icon_url FROM plan_role WHERE id=?";
        try {
            ResultSetHandler<String> rsh = new ScalarHandler<String>();
            return qr.query(con, sql, rsh, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
