package cn.tealc.wutheringwavestool.dao;

import cn.tealc.wutheringwavestool.model.plan.Plan;
import com.kuro.kujiequ.model.calculator.result.Cost;
import javafx.util.Pair;
import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName
 * @Description TODO
 * @Author Nefeli
 * @Data 2025/7/25 12:46
 * @Version 1.0
 */
public class PlanDao {
    private static final Logger LOG = LoggerFactory.getLogger(PlanDao.class);
    private final Connection con = JdbcUtils.getConnection();
    QueryRunner qr = new QueryRunner();

    private RowProcessor getRowProcessor() {
        Map<String, String> map = new HashMap<>();
        map.put("role_id", "roleId");
        map.put("item_id", "itemId");
        map.put("num", "num");
        BeanProcessor beanProcessor = new BeanProcessor(map);
        return new BasicRowProcessor(beanProcessor);
    }

    public Integer addOrUpdatePlan(Plan plan) {
        String sql = """
                INSERT INTO plan (role_id,item_id,num)
                VALUES (?,?,?)
                ON CONFLICT (role_id,item_id) DO UPDATE SET
                    num=?
                """;
        try {
            ResultSetHandler<Integer> rsh = new ScalarHandler<Integer>();
            return qr.insert(con, sql, rsh,
                    plan.getRoleId(), plan.getItemId(), plan.getNum(),
                    plan.getNum());
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public boolean deletePlan(int roleId) {
        String sql = "DELETE FROM plan WHERE role_id = ? ";
        try {
            int affectedRows = qr.update(con, sql, roleId);
            return affectedRows > 0;
        } catch (SQLException e) {
            LOG.error("Failed to delete plan for role: {}", roleId, e);
            return false;
        }
    }

    public List<Pair<String, Integer>> getItemsByRole(int roleId) {
        String sql = "SELECT item_id,num FROM plan WHERE role_id = ?";
        try {
            ResultSetHandler<List<Pair<String, Integer>>> rsh = new ScalarHandler<List<Pair<String, Integer>>>();
            return qr.query(con, sql, rsh, new BeanListHandler<>(Plan.class), roleId);
        } catch (SQLException e) {
            LOG.error("Failed to get plans by role: {}", roleId, e);
            return Collections.emptyList();
        }
    }

    public Map<String, Integer> getAllItemNumByItemId() {
        String sql = "SELECT item_id, SUM(num) AS total FROM plan GROUP BY item_id";
        try {
            // 查询结果作为Map列表：每个Map包含item_id和total
            List<Map<String, Object>> result = qr.query(con, sql, new MapListHandler());

            // 转换为ID->总数量映射
            Map<String, Integer> totals = new HashMap<>();
            for (Map<String, Object> row : result) {
                totals.put(
                        (String) row.get("item_id"),
                        ((Number) row.get("total")).intValue()
                );
            }
            return totals;
        } catch (SQLException e) {
            LOG.error("Failed to get total item quantities", e);
            return Collections.emptyMap();
        }
    }

    public List<Cost> getItemByType(int type) {
        String sql = "SELECT  num, id, name, icon_url, type, quality, preview " +
                "FROM plan p " +
                "JOIN plan_item i ON p.item_id = i.id " +
                "WHERE i.type = ?";
        try {
            return qr.query(con, sql, new BeanListHandler<>(Cost.class, PlanItemDao.getRowProcessor()), type);
        } catch (Exception e) {
            LOG.error("Failed to get total item quantities", e);
            return null;
        }
    }

    public Map getAllItemNum() {
        // SQL查询：按类型和名称分组，获取物品需求总量
        String sql = "SELECT i.type, i.name, SUM(p.num) AS total " +
                "FROM plan p " +
                "JOIN plan_item i ON p.item_id = i.id " +
                "GROUP BY i.type, i.name";

        try {
            // 执行查询
            List<Map<String, Object>> result = qr.query(con, sql, new MapListHandler());

            // 使用流API按类型分组
            return result.stream()
                    .collect(Collectors.groupingBy(
                            // 分组键：物品类型
                            row -> (Integer) row.get("type"),
                            // 下游收集器：将同类型物品转换为Pair列表
                            Collectors.mapping(
                                    row -> new Pair(
                                            (String) row.get("name"),
                                            ((Number) row.get("total")).intValue()
                                    ),
                                    Collectors.toList()
                            )
                    ));
        } catch (SQLException e) {
            LOG.error("Failed to get grouped item quantities by type", e);
            return Collections.emptyMap();
        }
    }

    public List<String> getAllRoleId() { // 修改返回类型为Integer
        String sql = "SELECT DISTINCT role_id FROM plan WHERE role_id IS NOT NULL";
        try {
            return qr.query(con, sql, new ColumnListHandler<String>("role_id"));
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList(); // 返回空集合代替null
        }
    }

    public List<String> getAllItemId() { // 修改返回类型为Integer
        String sql = "SELECT DISTINCT item_id FROM plan WHERE item_id IS NOT NULL";
        try {
            // 修正列名和泛型类型
            return qr.query(con, sql, new ColumnListHandler<String>("item_id"));
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList(); // 返回空集合代替null
        }
    }
}
