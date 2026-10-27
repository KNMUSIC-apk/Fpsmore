# Ethylene

Mod Fabric (client) tối ưu FPS cho Minecraft **26.3**, yêu cầu **Java 25**.

## Tính năng

### 1. Chunk LOD (giảm chi tiết chunk ở xa)
- Giữ chi tiết đầy đủ cho các vòng chunk gần người chơi (mặc định 2 vòng).
- Các vòng ngoài cùng được giảm pixel mỗi mặt block, từ 32 px xuống tối thiểu 1 px.
- Mặc định mỗi chunk render tăng thêm sẽ thêm 1 vòng bị giảm chi tiết:
  - render 4 → 2 vòng ngoài giảm chi tiết
  - render 5 → 3 vòng ngoài giảm chi tiết
- Có thể chỉnh bước thêm vòng (`lodChunkStep`) thành 0.5, 0.3, 0.2... và mức giảm pixel (`lodReduction`).

### 2. Giảm tải mob
- Đếm số mob trong bán kính `mobDensityRadius` quanh người chơi.
- Khi vượt `mobDensityThreshold`, mob được làm mờ tăng dần (tối đa `mobBlurPercent`%). Bối cảnh xung quanh không bị ảnh hưởng.
- Khi vượt ngưỡng, có thể giảm chuyển động của mob (`reduceMobMotion`).

## Trạng thái hiện tại

| Hạng mục | Trạng thái |
|---|---|
| Khung project Fabric (Gradle, `fabric.mod.json`, entrypoint client) | Đã viết |
| Cấu hình đọc/ghi `config/ethylene.json`, có kiểm tra giá trị | Đã viết, đã biên dịch |
| Logic LOD (số vòng, bậc pixel) và logic mật độ mob | Đã viết, **đã chạy test** (`EthyleneLogicTest`) |
| Áp dụng LOD vào pipeline render chunk của Minecraft | **Chưa làm** |
| Áp dụng làm mờ / giảm chuyển động vào render mob | **Chưa làm** |
| Menu cấu hình trong game | **Chưa làm** (chỉnh qua file JSON) |

Hiện tại mod chỉ nạp cấu hình và ghi log, chưa thay đổi gì trong game. Hai phần render còn lại cần viết mixin/render hook theo API của 26.3 và kiểm tra trong game.

## Build

Yêu cầu JDK 25 và Gradle 9.8.2.

**Cách 1: GitHub Actions (không cần cài gì trên máy)**
Vào tab **Actions** của repo, chọn workflow **Build & Generate Sources**, bấm **Run workflow**. Khi xong, tải artifact `ethylene-jar` (chứa file jar) hoặc `minecraft-generated-sources` (source Minecraft đã decompile).

**Cách 2: Trên máy của bạn**

```bash
# Tạo gradlew (chỉ cần làm một lần)
gradle wrapper --gradle-version 9.8.2
./gradlew build
```

File jar nằm trong `build/libs/ethylene-0.1.0.jar`.

**Lưu ý:** `loom_version` (`1.19.3`) đã được đối chiếu tag trên GitHub, nhưng chưa được kiểm chứng là build được Minecraft 26.3 cho đến khi chạy build thật.

## Kiểm tra logic (không cần Minecraft)

```bash
javac -d out src/main/java/com/knmusic/ethylene/lod/LodPolicy.java \
      src/main/java/com/knmusic/ethylene/mob/MobDensityPolicy.java \
      src/test/java/com/knmusic/ethylene/EthyleneLogicTest.java
java -ea -cp out com.knmusic.ethylene.EthyleneLogicTest
```

## Cấu hình (`config/ethylene.json`)

| Khóa | Mặc định | Ý nghĩa |
|---|---|---|
| `lodEnabled` | `true` | Bật/tắt chunk LOD |
| `fullDetailChunks` | `2` | Số vòng gần người chơi giữ chi tiết đầy đủ |
| `lodChunkStep` | `1.0` | Số vòng LOD thêm cho mỗi chunk render tăng thêm (0.0–4.0) |
| `lodReduction` | `1.0` | Mức giảm pixel: 0 = không giảm, 1 = vòng xa nhất còn 1 px |
| `mobBlurEnabled` | `true` | Bật/tắt làm mờ mob |
| `mobBlurPercent` | `50` | Mức làm mờ tối đa (0–100%) |
| `mobDensityThreshold` | `40` | Số mob trong bán kính trên mức này thì bắt đầu giảm tải |
| `mobDensityRadius` | `32` | Bán kính (block) để đếm mật độ mob |
| `reduceMobMotion` | `true` | Bật/tắt giảm chuyển động mob khi quá đông |

Giá trị ngoài khoảng hợp lệ sẽ được đưa về giới hạn gần nhất khi tải.

## Cấu trúc

```
src/main/java/com/knmusic/ethylene/
├── EthyleneClient.java          # Entrypoint client, nạp cấu hình
├── config/EthyleneConfig.java   # Cấu hình JSON
├── lod/LodPolicy.java           # Logic chunk LOD (thuần Java)
└── mob/MobDensityPolicy.java    # Logic mật độ mob (thuần Java)
src/test/java/com/knmusic/ethylene/
└── EthyleneLogicTest.java       # Kiểm tra logic LOD và mob
```
