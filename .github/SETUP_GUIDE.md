# 🚀 CI/CD Setup Guide for BankingApp

This guide explains how to set up and use the CI/CD workflows for the BankingApp project.

## 📋 Workflows Overview

### 1. **Android CI/CD - Build & Test** (`android-ci.yml`)
**Triggers**: `push` to master/main/develop, `pull_request`

- ✅ Checks out code
- ✅ Sets up JDK 11
- ✅ Builds project with Gradle
- ✅ Runs unit tests
- ✅ Runs lint analysis
- ✅ Builds Release APK
- ✅ Uploads APK and lint reports as artifacts
- ✅ Comments on PR with build status

### 2. **Android Release & Deploy** (`android-release.yml`)
**Triggers**: `push` to tags matching `v*` (e.g., v1.0.0)

- ✅ Builds Release APK and App Bundle (AAB)
- ✅ Optionally signs APK (if signing keys configured)
- ✅ Creates GitHub Release with assets
- ✅ Sends Slack notification (optional)
- ✅ Uploads artifacts for 90 days

### 3. **Code Quality Checks** (`code-quality.yml`)
**Triggers**: `push` to master/main/develop, `pull_request`

- ✅ Runs lint analysis
- ✅ Runs unit tests with coverage
- ✅ Uploads coverage to Codecov
- ✅ Archives reports

---

## 🔧 Configuration

### Basic Setup (No Additional Configuration Needed)
The CI/CD workflows will run automatically when you:
1. Push code to `master`, `main`, or `develop`
2. Create a pull request
3. Create a tag starting with `v` (e.g., `v1.0.0`)

### Optional: APK Signing

To automatically sign releases, add these GitHub Secrets:

1. Go to **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

Add the following secrets:

- **SIGNING_KEY**: Base64-encoded keystore file
  ```bash
  base64 -i your_keystore.jks | tr -d '\n' | pbcopy
  ```
- **KEYSTORE_PASSWORD**: Your keystore password
- **KEY_PASSWORD**: Your key password
- **KEY_ALIAS**: Your key alias (usually "key0" or similar)

### Optional: Slack Notifications

Add this secret for release notifications:

- **SLACK_WEBHOOK**: Your Slack incoming webhook URL

---

## 📝 Usage Guide

### Creating a Release

To create a release that triggers the deployment workflow:

```bash
# Tag the current commit
git tag v1.0.0

# Push the tag to GitHub
git push origin v1.0.0
```

The workflow will:
1. Build the APK and AAB
2. Create a GitHub Release
3. Attach the APK and AAB files
4. Send a Slack notification (if configured)

### Monitoring Builds

1. Go to your repository
2. Click **Actions** tab
3. Select the workflow run to see details
4. Check **Artifacts** for generated APK/reports

### Downloading Artifacts

After a successful build:
1. Go to **Actions** → Select the workflow run
2. Scroll down to **Artifacts**
3. Download APK or reports

---

## 🔍 Troubleshooting

### Build fails with "Permission denied: ./gradlew"
- The workflow automatically grants permissions, but ensure gradlew is executable locally:
  ```bash
  chmod +x gradlew
  ```

### Tests fail in CI but pass locally
- Ensure JDK 11 is used (same as CI)
- Check for environment-specific issues
- Review test logs in artifacts

### Release not created
- Verify the tag follows the format `v*` (e.g., `v1.0.0`)
- Check that the tag is pushed with: `git push origin v1.0.0`
- Ensure GitHub token has write permissions (default for repos)

### APK not signing automatically
- Check that all signing secrets are configured
- Verify the keystore file is valid and not corrupted
- Remove signing section if not needed

---

## 📚 Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Android Gradle Plugin](https://developer.android.com/build)
- [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)

---

## ✅ Checklist

- [ ] Workflows are created in `.github/workflows/`
- [ ] Default branch is set to `master`
- [ ] Tests pass locally before pushing
- [ ] First PR/push triggers CI automatically
- [ ] Artifacts are available after build
- [ ] Release tag is created and pushed
- [ ] GitHub Release is automatically created

---

## 🎯 Next Steps

1. Push this setup to your repository
2. Create a test PR to verify CI runs
3. Create a test release tag to verify deployment
4. Configure optional secrets for signing and Slack
5. Monitor your first automatic build!

Happy building! 🎉
