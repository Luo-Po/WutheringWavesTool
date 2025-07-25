package cn.tealc.wutheringwavestool.dao;

import cn.tealc.wutheringwavestool.model.game.GameRecord;
import com.kuro.kujiequ.model.calculator.result.Cost;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.BeanHandler;
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
public class PlanItemDao {
    private static final Logger LOG = LoggerFactory.getLogger(PlanItemDao.class);
    private final Connection con = JdbcUtils.getConnection();
    QueryRunner qr = new QueryRunner();

    public static RowProcessor getRowProcessor() {
        Map<String, String> map = new HashMap<>();
        map.put("id", "id");
        map.put("name", "name");
        map.put("icon_url", "iconUrl");
        map.put("num", "num");
        map.put("type", "type");
        map.put("quality", "quality");
        map.put("isPreview", "isPreview");
        BeanProcessor beanProcessor = new BeanProcessor(map);
        return new BasicRowProcessor(beanProcessor);
    }

    public Cost getItemById(String id) {
        QueryRunner qr = new QueryRunner();
        String sql = "SELECT * FROM plan_item WHERE id = ?";
        try {
            LOG.debug("..............................{}", qr.query(con, sql, new BeanHandler<>(Cost.class, getRowProcessor()), id));
            return qr.query(con, sql, new BeanHandler<>(Cost.class, getRowProcessor()), id);
        } catch (SQLException e) {
            LOG.error("..............................{}", e.getMessage(), e);
            return null;
        }
    }

    public Integer addOrUpdatePlanItem(Cost item) {
        String sql = """
                INSERT INTO plan_item (id,name,icon_url,type,quality,preview)
                VALUES (?,?,?,?,?,?)
                ON CONFLICT (id) DO UPDATE SET
                    name=?,
                    icon_url=?,
                    type=?,
                    quality=?,
                    preview=?
                """;
        try {
            ResultSetHandler<Integer> rsh = new ScalarHandler<Integer>();
            return qr.insert(con, sql, rsh,
                    item.getId(), item.getName(), item.getIconUrl(), item.getType(), item.getQuality(), item.isPreview(),
                    item.getName(), item.getIconUrl(), item.getType(), item.getQuality(), item.isPreview());
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public String getNameById(String id) {
        String sql = "SELECT DISTINCT name FROM plan_item WHERE id=?";
        try {
            ResultSetHandler<String> rsh = new ScalarHandler<String>();
            return qr.query(con, sql, rsh, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getIconById(String id) {
        String sql = "SELECT DISTINCT icon_url FROM plan_item WHERE id=?";
        try {
            ResultSetHandler<String> rsh = new ScalarHandler<String>();
            return qr.query(con, sql, rsh, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getIconByName(String name) {
        String sql = "SELECT DISTINCT icon_url FROM plan_item WHERE name=?";
        try {
            ResultSetHandler<String> rsh = new ScalarHandler<String>();
            return qr.query(con, sql, rsh, name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTypeById(String id) {
        String sql = "SELECT DISTINCT type FROM plan_item WHERE id=?";
        try {
            ResultSetHandler<Integer> rsh = new ScalarHandler<Integer>();
            return qr.query(con, sql, rsh, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getQualityById(String id) {
        String sql = "SELECT DISTINCT quality FROM plan_item WHERE id=?";
        try {
            ResultSetHandler<Integer> rsh = new ScalarHandler<Integer>();
            return qr.query(con, sql, rsh, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getPreviewById(String id) {
        String sql = "SELECT DISTINCT preview FROM plan_item WHERE id=?";
        try {
            ResultSetHandler<Boolean> rsh = new ScalarHandler<Boolean>();
            return qr.query(con, sql, rsh, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
