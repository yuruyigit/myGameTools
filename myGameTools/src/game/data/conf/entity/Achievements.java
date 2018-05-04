package game.data.conf.entity;

public class Achievements {
    private Integer id;

    private String name;

    private Integer preId;

    private Integer nextId;

    private Integer type;

    private Integer kind;

    private Integer skipType;

    private String description;

    private Integer isInherit;

    private Integer target;

    private Integer target2;

    private Integer coin;

    private Integer rewardId;

    private Integer icon;

    private Integer openLevel;

    private Integer index;

    private Integer isOne;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getPreId() {
        return preId;
    }

    public void setPreId(Integer preId) {
        this.preId = preId;
    }

    public Integer getNextId() {
        return nextId;
    }

    public void setNextId(Integer nextId) {
        this.nextId = nextId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getKind() {
        return kind;
    }

    public void setKind(Integer kind) {
        this.kind = kind;
    }

    public Integer getSkipType() {
        return skipType;
    }

    public void setSkipType(Integer skipType) {
        this.skipType = skipType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getIsInherit() {
        return isInherit;
    }

    public void setIsInherit(Integer isInherit) {
        this.isInherit = isInherit;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public Integer getTarget2() {
        return target2;
    }

    public void setTarget2(Integer target2) {
        this.target2 = target2;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public Integer getRewardId() {
        return rewardId;
    }

    public void setRewardId(Integer rewardId) {
        this.rewardId = rewardId;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public Integer getOpenLevel() {
        return openLevel;
    }

    public void setOpenLevel(Integer openLevel) {
        this.openLevel = openLevel;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getIsOne() {
        return isOne;
    }

    public void setIsOne(Integer isOne) {
        this.isOne = isOne;
    }
}