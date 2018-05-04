package game.data.conf.entity;

public class BookworldCommons {
    private Integer id;

    private Integer type;

    private Integer kind;

    private String name;

    private Integer level;

    private Integer needMainLv;

    private Integer cd;

    private String needRes;

    private Integer formationLimit;

    private Integer helperLimit;

    private String storageLimit;

    private Integer task;

    private Integer primaryPower;

    private Integer primaryTaskTime;

    private Integer primaryTaskTimeFirst;

    private String primaryRewards;

    private Integer mediumPower;

    private Integer mediumTaskTime;

    private String mediumRewards;

    private Integer seniorPower;

    private Integer seniorTaskTime;

    private String seniorRewards;

    private String seniorRewardsSpecial;

    private Integer display;

    private String coordinate;

    private String description;

    private String levelupDescription;

    private Integer primaryTaskDoubleCost;

    private Integer mediumTaskDoubleCost;

    private Integer seniorTaskDoubleCost;

    private String effectName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getNeedMainLv() {
        return needMainLv;
    }

    public void setNeedMainLv(Integer needMainLv) {
        this.needMainLv = needMainLv;
    }

    public Integer getCd() {
        return cd;
    }

    public void setCd(Integer cd) {
        this.cd = cd;
    }

    public String getNeedRes() {
        return needRes;
    }

    public void setNeedRes(String needRes) {
        this.needRes = needRes == null ? null : needRes.trim();
    }

    public Integer getFormationLimit() {
        return formationLimit;
    }

    public void setFormationLimit(Integer formationLimit) {
        this.formationLimit = formationLimit;
    }

    public Integer getHelperLimit() {
        return helperLimit;
    }

    public void setHelperLimit(Integer helperLimit) {
        this.helperLimit = helperLimit;
    }

    public String getStorageLimit() {
        return storageLimit;
    }

    public void setStorageLimit(String storageLimit) {
        this.storageLimit = storageLimit == null ? null : storageLimit.trim();
    }

    public Integer getTask() {
        return task;
    }

    public void setTask(Integer task) {
        this.task = task;
    }

    public Integer getPrimaryPower() {
        return primaryPower;
    }

    public void setPrimaryPower(Integer primaryPower) {
        this.primaryPower = primaryPower;
    }

    public Integer getPrimaryTaskTime() {
        return primaryTaskTime;
    }

    public void setPrimaryTaskTime(Integer primaryTaskTime) {
        this.primaryTaskTime = primaryTaskTime;
    }

    public Integer getPrimaryTaskTimeFirst() {
        return primaryTaskTimeFirst;
    }

    public void setPrimaryTaskTimeFirst(Integer primaryTaskTimeFirst) {
        this.primaryTaskTimeFirst = primaryTaskTimeFirst;
    }

    public String getPrimaryRewards() {
        return primaryRewards;
    }

    public void setPrimaryRewards(String primaryRewards) {
        this.primaryRewards = primaryRewards == null ? null : primaryRewards.trim();
    }

    public Integer getMediumPower() {
        return mediumPower;
    }

    public void setMediumPower(Integer mediumPower) {
        this.mediumPower = mediumPower;
    }

    public Integer getMediumTaskTime() {
        return mediumTaskTime;
    }

    public void setMediumTaskTime(Integer mediumTaskTime) {
        this.mediumTaskTime = mediumTaskTime;
    }

    public String getMediumRewards() {
        return mediumRewards;
    }

    public void setMediumRewards(String mediumRewards) {
        this.mediumRewards = mediumRewards == null ? null : mediumRewards.trim();
    }

    public Integer getSeniorPower() {
        return seniorPower;
    }

    public void setSeniorPower(Integer seniorPower) {
        this.seniorPower = seniorPower;
    }

    public Integer getSeniorTaskTime() {
        return seniorTaskTime;
    }

    public void setSeniorTaskTime(Integer seniorTaskTime) {
        this.seniorTaskTime = seniorTaskTime;
    }

    public String getSeniorRewards() {
        return seniorRewards;
    }

    public void setSeniorRewards(String seniorRewards) {
        this.seniorRewards = seniorRewards == null ? null : seniorRewards.trim();
    }

    public String getSeniorRewardsSpecial() {
        return seniorRewardsSpecial;
    }

    public void setSeniorRewardsSpecial(String seniorRewardsSpecial) {
        this.seniorRewardsSpecial = seniorRewardsSpecial == null ? null : seniorRewardsSpecial.trim();
    }

    public Integer getDisplay() {
        return display;
    }

    public void setDisplay(Integer display) {
        this.display = display;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate == null ? null : coordinate.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getLevelupDescription() {
        return levelupDescription;
    }

    public void setLevelupDescription(String levelupDescription) {
        this.levelupDescription = levelupDescription == null ? null : levelupDescription.trim();
    }

    public Integer getPrimaryTaskDoubleCost() {
        return primaryTaskDoubleCost;
    }

    public void setPrimaryTaskDoubleCost(Integer primaryTaskDoubleCost) {
        this.primaryTaskDoubleCost = primaryTaskDoubleCost;
    }

    public Integer getMediumTaskDoubleCost() {
        return mediumTaskDoubleCost;
    }

    public void setMediumTaskDoubleCost(Integer mediumTaskDoubleCost) {
        this.mediumTaskDoubleCost = mediumTaskDoubleCost;
    }

    public Integer getSeniorTaskDoubleCost() {
        return seniorTaskDoubleCost;
    }

    public void setSeniorTaskDoubleCost(Integer seniorTaskDoubleCost) {
        this.seniorTaskDoubleCost = seniorTaskDoubleCost;
    }

    public String getEffectName() {
        return effectName;
    }

    public void setEffectName(String effectName) {
        this.effectName = effectName == null ? null : effectName.trim();
    }
}