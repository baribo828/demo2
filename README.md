# NFC Employee Attribute Proof Demo

Android HCEを使い、Androidスマホを「社員属性証明カード」のように動かすPoCです。

## できること

- カード役AndroidがNFCで「社員である」属性だけを提示します。
- リーダー役AndroidがNFCで属性証明データを読み取り、以下を表示します。
  - 社員確認OK / NG
  - 証明種別
  - 提示属性：社員である
  - 発行者
  - 証明ID
  - 有効期限
  - 署名検証：OK（デモ）
  - 失効確認：デモでは省略

## アプリ構成

- `card-app`: 社員属性証明カード役
- `reader-app`: 社員証明リーダー役

## デモの使い方

1. `card-app-debug.apk` をカード役Androidにインストールします。
2. `reader-app-debug.apk` をリーダー役Androidにインストールします。
3. 両方のAndroidでNFCをONにします。
4. 両方のアプリを起動します。
5. 端末の背面同士を近づけます。
6. リーダー側に「社員確認OK」が表示されます。

## GitHub ActionsでAPK化する場合

このリポジトリには `.github/workflows/android-apk.yml` を含めています。
GitHubにアップロードするとActionsからAPKをビルドできます。

Artifacts名は以下です。

- `nfc-employee-proof-debug-apks`

## 注意

これはデモ用です。本番用途ではありません。
本番化する場合は少なくとも以下が必要です。

- Android KeystoreまたはSecure Elementを使った秘密鍵管理
- 短命トークン生成
- nonceによるリプレイ対策
- サーバーまたはリーダー側での署名検証
- 失効確認
- 端末登録・停止
- 社員マスタ・権限管理
- 利用ログ・監査ログ
- Play Integrity API等による端末真正性確認
